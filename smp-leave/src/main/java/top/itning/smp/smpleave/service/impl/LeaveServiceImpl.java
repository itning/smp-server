package top.itning.smp.smpleave.service.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smpleave.client.InfoClient;
import top.itning.smp.smpleave.client.entity.StudentUser;
import top.itning.smp.smpleave.dao.LeaveDao;
import top.itning.smp.smpleave.dao.LeaveReasonDao;
import top.itning.smp.smpleave.dto.LeaveDTO;
import top.itning.smp.smpleave.dto.SearchDTO;
import top.itning.smp.smpleave.entity.Leave;
import top.itning.smp.smpleave.entity.LeaveReason;
import top.itning.smp.smpleave.entity.LeaveType;
import top.itning.smp.smpleave.entity.User;
import top.itning.smp.smpleave.exception.DateRangeException;
import top.itning.smp.smpleave.exception.NullFiledException;
import top.itning.smp.smpleave.exception.SecurityException;
import top.itning.smp.smpleave.exception.UnexpectedException;
import top.itning.smp.smpleave.security.LoginUser;
import top.itning.smp.smpleave.service.LeaveService;
import top.itning.smp.smpleave.util.DateUtils;
import top.itning.smp.smpleave.util.OrikaUtils;
import top.itning.utils.tuple.Tuple2;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static top.itning.smp.smpleave.util.DateUtils.*;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LeaveServiceImpl implements LeaveService {
    private static final Logger logger = LoggerFactory.getLogger(LeaveServiceImpl.class);
    private final LeaveDao leaveDao;
    private final InfoClient infoClient;
    private final LeaveReasonDao leaveReasonDao;

    @Autowired
    public LeaveServiceImpl(LeaveDao leaveDao, InfoClient infoClient, LeaveReasonDao leaveReasonDao) {
        this.leaveDao = leaveDao;
        this.infoClient = infoClient;
        this.leaveReasonDao = leaveReasonDao;
    }

    @Override
    public Page<LeaveDTO> getLeaves(Pageable pageable, Boolean status, LoginUser loginUser) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        return leaveDao.findAll((Specification<Leave>) (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (Objects.isNull(status)) {
                list.add(cb.isNull(root.get("status")));
            } else {
                list.add(cb.equal(root.get("status"), status));
            }
            Join<Leave, User> userJoin = root.join("user", JoinType.INNER);
            Join<User, top.itning.smp.smpleave.entity.StudentUser> studentUserJoin = userJoin.join("studentUser", JoinType.INNER);

            list.add(cb.equal(studentUserJoin.get("belongCounselorId"), user.getId()));

            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        }, pageable).map(mapLeave2LeaveDto());
    }

    @Override
    public Leave newLeave(Leave leave, LoginUser loginUser) {
        if (!ObjectUtils.allNotNull(leave.getStartTime(), leave.getEndTime(), leave.getReason(), leave.getLeaveType())) {
            throw new NullFiledException("请假信息不能为空", HttpStatus.BAD_REQUEST);
        }
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });

        leave.setStartTime(localDateTime2Date(with0Time(date2LocalDateTime(leave.getStartTime()))));
        leave.setEndTime(localDateTime2Date(with59Time(date2LocalDateTime(leave.getEndTime()))));

        List<Leave> leaveList = leaveDao.findAllByUser(user);
        if (!leaveList.isEmpty()) {
            leaveList.forEach(dataBaseLeave -> {
                // 请同样类型的假并且时间相同的拒绝申请
                if (leave.getLeaveType() == dataBaseLeave.getLeaveType() && DateUtils.isDateCross(dataBaseLeave.getStartTime(), dataBaseLeave.getEndTime(), leave.getStartTime(), leave.getEndTime())) {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
                    String start = LocalDateTime.ofInstant(dataBaseLeave.getStartTime().toInstant(), ZoneId.of("Asia/Shanghai")).format(dateTimeFormatter);
                    String end = LocalDateTime.ofInstant(dataBaseLeave.getEndTime().toInstant(), ZoneId.of("Asia/Shanghai")).format(dateTimeFormatter);
                    throw new DateRangeException(start + "至" + end + "您已经请过假了");
                }
            });
        }
        leave.setId(null);
        leave.setUser(user);
        leave.setStatus(null);
        leave.setLeaveReasonList(null);
        return leaveDao.save(leave);
    }

    @Override
    public Page<LeaveDTO> search(SearchDTO searchDTO, Pageable pageable, Boolean status, LoginUser loginUser) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        return leaveDao.findAll((Specification<Leave>) (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            Join<Leave, User> userJoin = root.join("user", JoinType.INNER);

            if (StringUtils.isNumeric(searchDTO.getKey())) {
                logger.debug("search student id: {}", searchDTO.getKey());
                Join<User, top.itning.smp.smpleave.entity.StudentUser> studentUserJoin = userJoin.join("studentUser", JoinType.INNER);
                list.add(cb.like(studentUserJoin.get("studentId"), "%" + searchDTO.getKey() + "%"));
            } else {
                logger.debug("search user name: {}", searchDTO.getKey());
                list.add(cb.like(userJoin.get("name"), "%" + searchDTO.getKey() + "%"));
            }

            if (Objects.nonNull(searchDTO.getLeaveType())) {
                logger.debug("search leave type: {}", searchDTO.getLeaveType());
                list.add(cb.equal(root.get("leaveType"), searchDTO.getLeaveType()));
            }

            if (Objects.isNull(status)) {
                list.add(cb.isNull(root.get("status")));
            } else {
                list.add(cb.equal(root.get("status"), status));
            }

            if (ObjectUtils.allNotNull(searchDTO.getStartTime(), searchDTO.getEndTime())) {
                logger.debug("search between start time {} {}", searchDTO.getStartTime(), searchDTO.getEndTime());
                dateIntervalQuery(list, cb, root, "startTime", searchDTO.getStartTime(), searchDTO.getEndTime());
            }

            if (Objects.nonNull(searchDTO.getEffective())) {
                if (searchDTO.getEffective()) {
                    logger.debug("search < end time");
                    dateIntervalQuery(list, cb, root, "endTime", localDateTime2Date(with0Time(getNow())), null);
                } else {
                    logger.debug("search > end time");
                    dateIntervalQuery(list, cb, root, "endTime", null, localDateTime2Date(with0Time(getNow())));
                }
            }
            Join<User, top.itning.smp.smpleave.entity.StudentUser> studentUserJoin = userJoin.join("studentUser", JoinType.INNER);
            list.add(cb.equal(studentUserJoin.get("belongCounselorId"), user.getId()));

            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        }, pageable)
                .map(leave -> {
                    StudentUser studentUser = infoClient.getStudentUserInfoByUserName(leave.getUser().getUsername()).orElse(null);
                    LeaveDTO leaveDTO = OrikaUtils.a2b(leave, LeaveDTO.class);
                    leaveDTO.setStudentUser(studentUser);
                    return leaveDTO;
                });
    }

    @Override
    public LeaveReason newComment(String leaveId, String comment, LoginUser loginUser) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        if (StringUtils.isAnyBlank(leaveId, comment)) {
            throw new NullFiledException("参数为空", HttpStatus.BAD_REQUEST);
        }
        Leave leave = leaveDao.findById(leaveId).orElseThrow(() -> new NullFiledException("请假信息不存在", HttpStatus.BAD_REQUEST));
        List<LeaveReason> leaveReasonList = leave.getLeaveReasonList();
        if (leaveReasonList == null) {
            leaveReasonList = new ArrayList<>(1);
        }
        LeaveReason leaveReason = new LeaveReason();
        leaveReason.setFromUser(user);
        leaveReason.setComment(comment);
        leaveReasonList.add(leaveReason);
        leave.setLeaveReasonList(leaveReasonList);
        return leaveReasonDao.save(leaveReason);
    }

    @Override
    public void leaveCheckStatusChange(String leaveId, boolean status, LoginUser loginUser) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        Leave leave = leaveDao.findById(leaveId).orElseThrow(() -> new NullFiledException("请假ID不存在", HttpStatus.BAD_REQUEST));
        StudentUser studentUser = infoClient.getStudentUserInfoByUserName(leave.getUser().getUsername()).orElseThrow(() -> new NullFiledException("没有这个学生", HttpStatus.NOT_FOUND));
        if (!user.getId().equals(studentUser.getBelongCounselorId())) {
            throw new SecurityException("审批失败", HttpStatus.FORBIDDEN);
        }
        leave.setStatus(status);
        leaveDao.save(leave);
    }

    @Override
    public long countInEffectLeaves(Date date, String username) {
        return leaveDao.count(getLeaveSpecification(date, username));
    }

    @Override
    public boolean isUserLeaveToday(String userName, LeaveType leaveType) {
        return leaveDao.count((Specification<Leave>) (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            Tuple2<Date, Date> dateRange = getDateRange(new Date());
            dateIntervalQuery(list, cb, root, "startTime", null, dateRange.getT2());
            dateIntervalQuery(list, cb, root, "endTime", dateRange.getT1(), null);
            list.add(cb.equal(root.get("status"), true));
            list.add(cb.or(
                    cb.equal(root.get("leaveType"), leaveType),
                    cb.equal(root.get("leaveType"), LeaveType.ALL_LEAVE)));
            Join<Leave, User> userJoin = root.join("user", JoinType.INNER);
            list.add(cb.equal(userJoin.get("username"), userName));
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        }) != 0;
    }

    @Override
    public List<LeaveDTO> getLeaves(Date whereDay, @Nullable String username) {
        return leaveDao.findAll(getLeaveSpecification(whereDay, username))
                .stream()
                .map(mapLeave2LeaveDto())
                .collect(Collectors.toList());
    }

    /**
     * 根据whereDay查询请假通过的寝室假或全假
     *
     * @param whereDay 那天开始
     * @return Specification
     */
    private Specification<Leave> getLeaveSpecification(Date whereDay, @Nullable String username) {
        return (Specification<Leave>) (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            Tuple2<Date, Date> dateRange = getDateRange(whereDay);
            dateIntervalQuery(list, cb, root, "startTime", null, dateRange.getT2());
            dateIntervalQuery(list, cb, root, "endTime", dateRange.getT1(), null);
            list.add(cb.or(
                    cb.equal(root.get("leaveType"), LeaveType.ROOM_LEAVE),
                    cb.equal(root.get("leaveType"), LeaveType.ALL_LEAVE)));
            list.add(cb.equal(root.get("status"), true));
            if (username != null) {
                User user = infoClient.getUserInfoByUserName(username).orElseThrow(() -> new NullFiledException("用户不存在", HttpStatus.NOT_FOUND));
                Join<Leave, User> userJoin = root.join("user", JoinType.INNER);
                Join<User, top.itning.smp.smpleave.entity.StudentUser> studentUserJoin = userJoin.join("studentUser", JoinType.INNER);
                list.add(cb.equal(studentUserJoin.get("belongCounselorId"), user.getId()));
            }
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        };
    }

    @Override
    public Page<LeaveDTO> getStudentLeaves(Pageable pageable, LoginUser loginUser) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        return leaveDao.findAllByUser(user, pageable).map(mapLeave2LeaveDto());
    }

    @Override
    public long countAllLeave(Date startDate, Date endDate, String username) {
        return leaveDao.count((Specification<Leave>) (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            dateIntervalQuery(list, cb, root, "startTime", null, endDate);
            dateIntervalQuery(list, cb, root, "endTime", startDate, null);
            list.add(cb.equal(root.get("status"), true));
            if (username != null) {
                User user = infoClient.getUserInfoByUserName(username).orElseThrow(() -> new NullFiledException("用户不存在", HttpStatus.NOT_FOUND));
                Join<Leave, User> userJoin = root.join("user", JoinType.INNER);
                Join<User, top.itning.smp.smpleave.entity.StudentUser> studentUserJoin = userJoin.join("studentUser", JoinType.INNER);
                list.add(cb.equal(studentUserJoin.get("belongCounselorId"), user.getId()));
            }
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        });
    }

    @Override
    public void delLeaveInfo(String counselorUsername, String studentUserName) {
        User counselorUser = infoClient.getUserInfoByUserName(counselorUsername).orElseThrow(() -> new NullFiledException("用户不存在", HttpStatus.NOT_FOUND));
        top.itning.smp.smpleave.client.entity.StudentUser studentUser = infoClient.getStudentUserInfoByUserName(studentUserName).orElseThrow(() -> new NullFiledException("学生不存在", HttpStatus.NOT_FOUND));
        if (!studentUser.getBelongCounselorId().equals(counselorUser.getId())) {
            throw new SecurityException("无法删除", HttpStatus.FORBIDDEN);
        }
        User user = new User();
        user.setId(studentUser.getId());
        leaveDao.findAllByUser(user)
                .stream()
                .peek(leave -> leaveReasonDao.deleteAll(leave.getLeaveReasonList()))
                .forEach(leaveDao::delete);
    }

    /**
     * 转换Leave到LeaveDTO
     *
     * @return LeaveDTO
     */
    private Function<Leave, LeaveDTO> mapLeave2LeaveDto() {
        return leave -> {
            StudentUser studentUser = infoClient.getStudentUserInfoByUserName(leave.getUser().getUsername()).orElse(null);
            LeaveDTO leaveDTO = OrikaUtils.a2b(leave, LeaveDTO.class);
            leaveDTO.setStudentUser(studentUser);
            leaveDTO.setLeaveReasonList(leaveDTO.getLeaveReasonList().stream().sorted(Comparator.comparing(LeaveReason::getGmtCreate).reversed()).collect(Collectors.toList()));
            return leaveDTO;
        };
    }

    /**
     * 日期区间查询
     *
     * @param list      条件集合
     * @param cb        CriteriaBuilder
     * @param root      Root Staff
     * @param field     查询字段
     * @param startDate 开始日期
     * @param endDate   结束日期
     */
    private void dateIntervalQuery(List<Predicate> list, CriteriaBuilder cb, Root<Leave> root, String field, Date startDate, Date endDate) {
        //有开始有结束
        if (startDate != null && endDate != null) {
            logger.debug("dateIntervalQuery::已获取到开始和结束时间");
            list.add(cb.between(root.get(field), startDate, endDate));
        } else {
            //只有开始时间
            if (startDate != null) {
                logger.debug("dateIntervalQuery::已获取到开始时间");
                list.add(cb.between(root.get(field), startDate, MAX_DATE));
            } else {//只有结束时间
                logger.debug("dateIntervalQuery::已获取到结束时间");
                list.add(cb.between(root.get(field), MIN_DATE, endDate));
            }
        }
    }
}
