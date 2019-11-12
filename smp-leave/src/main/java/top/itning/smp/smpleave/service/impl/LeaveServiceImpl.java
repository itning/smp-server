package top.itning.smp.smpleave.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smpleave.client.InfoClient;
import top.itning.smp.smpleave.client.entity.StudentUser;
import top.itning.smp.smpleave.dao.LeaveDao;
import top.itning.smp.smpleave.dto.LeaveDTO;
import top.itning.smp.smpleave.entity.Leave;
import top.itning.smp.smpleave.entity.User;
import top.itning.smp.smpleave.exception.UnexpectedException;
import top.itning.smp.smpleave.security.LoginUser;
import top.itning.smp.smpleave.service.LeaveService;
import top.itning.smp.smpleave.util.OrikaUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LeaveServiceImpl implements LeaveService {
    private static final Logger logger = LoggerFactory.getLogger(LeaveServiceImpl.class);

    private final LeaveDao leaveDao;
    private final InfoClient infoClient;

    @Autowired
    public LeaveServiceImpl(LeaveDao leaveDao, InfoClient infoClient) {
        this.leaveDao = leaveDao;
        this.infoClient = infoClient;
    }

    @Override
    public Page<LeaveDTO> getLeaves(Pageable pageable) {
        return leaveDao.findAllByStatus(true, pageable).map(leave -> {
            StudentUser studentUser = infoClient.getStudentUserInfoByUserName(leave.getUser().getUsername()).orElse(null);
            LeaveDTO leaveDTO = OrikaUtils.a2b(leave, LeaveDTO.class);
            leaveDTO.setStudentUser(studentUser);
            return leaveDTO;
        });
    }

    @Override
    public Leave newLeave(Leave leave, LoginUser loginUser) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        leave.setId(null);
        leave.setUser(user);
        leave.setStatus(false);
        return leaveDao.save(leave);
    }

    @Override
    public Page<LeaveDTO> search(String key, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        List<LeaveDTO> leaves = leaveDao.findByKey("%" + key + "%", pageNumber * pageSize, pageSize, true)
                .parallelStream()
                .map(leave -> {
                    StudentUser studentUser = infoClient.getStudentUserInfoByUserName(leave.getUser().getUsername()).orElse(null);
                    LeaveDTO leaveDTO = OrikaUtils.a2b(leave, LeaveDTO.class);
                    leaveDTO.setStudentUser(studentUser);
                    return leaveDTO;
                }).collect(Collectors.toList());
        return new PageImpl<>(leaves, pageable, leaveDao.countByKey("%" + key + "%", pageNumber * pageSize, pageSize, true));
    }

    /**
     * 日期区间查询
     *
     * @param logger    日志工厂
     * @param list      条件集合
     * @param cb        CriteriaBuilder
     * @param root      Root Staff
     * @param field     查询字段
     * @param startDate 开始日期
     * @param endDate   结束日期
     */
    private void dateIntervalQuery(Logger logger, List<Predicate> list, CriteriaBuilder cb, Root<Leave> root, String field, Date startDate, Date endDate) {
        //有开始有结束
        if (startDate != null && endDate != null) {
            logger.info("dateIntervalQuery::已获取到开始和结束时间");
            list.add(cb.between(root.get(field), startDate, endDate));
        } else {
            Date minDate = null;
            Date maxDate = null;
            try {
                minDate = new SimpleDateFormat("yyyy-MM-dd").parse("2001-01-01");
                maxDate = new SimpleDateFormat("yyyy-MM-dd").parse("9999-12-31");
            } catch (ParseException e) {
                //不可能的异常
                logger.error("dateIntervalQuery::日期转换出现问题?" + e.getMessage());
            }
            //只有开始时间
            if (startDate != null) {
                logger.debug("dateIntervalQuery::已获取到开始时间");
                list.add(cb.between(root.get(field), startDate, maxDate));
            } else {//只有结束时间
                logger.debug("dateIntervalQuery::已获取到结束时间");
                list.add(cb.between(root.get(field), minDate, endDate));
            }
        }
    }
}
