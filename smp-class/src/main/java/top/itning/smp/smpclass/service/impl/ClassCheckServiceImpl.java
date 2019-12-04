package top.itning.smp.smpclass.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smpclass.client.InfoClient;
import top.itning.smp.smpclass.client.LeaveClient;
import top.itning.smp.smpclass.client.entity.LeaveType;
import top.itning.smp.smpclass.dao.StudentClassCheckDao;
import top.itning.smp.smpclass.dao.StudentClassCheckMetaDataDao;
import top.itning.smp.smpclass.dao.StudentClassDao;
import top.itning.smp.smpclass.dao.StudentClassUserDao;
import top.itning.smp.smpclass.dto.StudentClassCheckDTO;
import top.itning.smp.smpclass.entity.*;
import top.itning.smp.smpclass.exception.GpsException;
import top.itning.smp.smpclass.exception.NullFiledException;
import top.itning.smp.smpclass.exception.SecurityException;
import top.itning.smp.smpclass.exception.UnexpectedException;
import top.itning.smp.smpclass.security.LoginUser;
import top.itning.smp.smpclass.service.ClassCheckService;
import top.itning.smp.smpclass.util.GpsUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static top.itning.smp.smpclass.util.GpsUtils.*;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClassCheckServiceImpl implements ClassCheckService {
    private static final Logger logger = LoggerFactory.getLogger(ClassCheckServiceImpl.class);
    private final StudentClassCheckMetaDataDao studentClassCheckMetaDataDao;
    private final StudentClassCheckDao studentClassCheckDao;
    private final StudentClassUserDao studentClassUserDao;
    private final StudentClassDao studentClassDao;
    private final InfoClient infoClient;
    private final LeaveClient leaveClient;

    @Autowired
    public ClassCheckServiceImpl(StudentClassCheckMetaDataDao studentClassCheckMetaDataDao, StudentClassCheckDao studentClassCheckDao, StudentClassUserDao studentClassUserDao, StudentClassDao studentClassDao, InfoClient infoClient, LeaveClient leaveClient) {
        this.studentClassCheckMetaDataDao = studentClassCheckMetaDataDao;
        this.studentClassCheckDao = studentClassCheckDao;
        this.studentClassUserDao = studentClassUserDao;
        this.studentClassDao = studentClassDao;
        this.infoClient = infoClient;
        this.leaveClient = leaveClient;
    }

    @Override
    public Page<StudentClassCheck> getAllChecks(String studentClassId, LoginUser loginUser, Pageable pageable) {
        StudentClass studentClass = studentClassDao.findById(studentClassId).orElseThrow(() -> new NullFiledException("学生班级不存在", HttpStatus.NOT_FOUND));
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        return studentClassCheckDao.findAllByUserAndStudentClass(user, studentClass, pageable);
    }

    @Override
    public boolean canCheck(String studentClassId, LoginUser loginUser) {
        StudentClass studentClass = studentClassDao.findById(studentClassId).orElseThrow(() -> new NullFiledException("学生班级不存在", HttpStatus.NOT_FOUND));
        StudentClassCheckMetaData studentClassCheckMetaData = studentClassCheckMetaDataDao.findTopByStudentClassOrderByGmtCreateDesc(studentClass);
        if (studentClassCheckMetaData == null) {
            return false;
        }
        Date endTime = studentClassCheckMetaData.getEndTime();
        return endTime.after(new Date());
    }

    @Override
    public StudentClassCheck check(LoginUser loginUser, String studentClassId, double longitude, double latitude) {
        if (longitude > MAX_LONGITUDE || longitude < MIN_LONGITUDE || latitude > MAX_LATITUDE || latitude < MIN_LATITUDE) {
            throw new GpsException(longitude, latitude);
        }
        StudentClass studentClass = studentClassDao.findById(studentClassId).orElseThrow(() -> new NullFiledException("学生班级不存在", HttpStatus.NOT_FOUND));
        StudentClassCheckMetaData studentClassCheckMetaData = studentClassCheckMetaDataDao.findTopByStudentClassOrderByGmtCreateDesc(studentClass);
        if (studentClassCheckMetaData == null) {
            throw new NullFiledException("目前无法打卡", HttpStatus.BAD_REQUEST);
        }
        Date endTime = studentClassCheckMetaData.getEndTime();
        if (!endTime.after(new Date())) {
            throw new NullFiledException("目前无法打卡，教师未开启或已过期", HttpStatus.BAD_REQUEST);
        }
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        if (leaveClient.isLeave(user.getUsername(), LeaveType.CLASS_LEAVE)) {
            throw new NullFiledException("您今天已经请假了，无需打卡", HttpStatus.BAD_REQUEST);
        }
        float m = GpsUtils.calculateLineDistance(studentClassCheckMetaData.getLatitude(), studentClassCheckMetaData.getLongitude(),
                latitude, longitude);
        logger.debug("user {} student class id {} calculate line distance {} and set m {}", loginUser.getName(), studentClass.getId(), m, studentClassCheckMetaData.getM());
        if (m > studentClassCheckMetaData.getM()) {
            logger.debug("teacher longitude: {} latitude: {}", studentClassCheckMetaData.getLongitude(), studentClassCheckMetaData.getLatitude());
            logger.debug("user longitude: {} latitude: {}", longitude, latitude);
            throw new NullFiledException("你已超过教师" + (m - studentClassCheckMetaData.getM()) + "米，无法签到", HttpStatus.BAD_REQUEST);
        }
        if (studentClassCheckDao.existsByUserAndStudentClassCheckMetaData(user, studentClassCheckMetaData)) {
            throw new NullFiledException("你已经签过到了，不能重复签到", HttpStatus.BAD_REQUEST);
        }
        StudentClassCheck studentClassCheck = new StudentClassCheck();
        studentClassCheck.setUser(user);
        studentClassCheck.setStudentClass(studentClass);
        studentClassCheck.setLongitude(longitude);
        studentClassCheck.setLatitude(latitude);
        studentClassCheck.setStudentClassCheckMetaData(studentClassCheckMetaData);
        studentClassCheck.setCheckTime(new Date());
        return studentClassCheckDao.save(studentClassCheck);
    }

    @Override
    public StudentClassCheckMetaData newCheck(LoginUser loginUser, double longitude, double latitude, String studentClassId, float m, Date startTime, Date endTime) {
        if (longitude > MAX_LONGITUDE || longitude < MIN_LONGITUDE || latitude > MAX_LATITUDE || latitude < MIN_LATITUDE) {
            throw new GpsException(longitude, latitude);
        }
        StudentClass studentClass = studentClassDao.findById(studentClassId).orElseThrow(() -> new NullFiledException("学生班级不存在", HttpStatus.NOT_FOUND));
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        if (!studentClass.getUser().getId().equals(user.getId())) {
            throw new SecurityException("创建失败", HttpStatus.FORBIDDEN);
        }
        if (m < 1) {
            m = 1;
        }
        StudentClassCheckMetaData studentClassCheckMetaData = new StudentClassCheckMetaData();
        studentClassCheckMetaData.setStartTime(startTime);
        studentClassCheckMetaData.setEndTime(endTime);
        studentClassCheckMetaData.setLongitude(longitude);
        studentClassCheckMetaData.setLatitude(latitude);
        studentClassCheckMetaData.setM(m);
        studentClassCheckMetaData.setStudentClass(studentClass);
        return studentClassCheckMetaDataDao.save(studentClassCheckMetaData);
    }

    @Override
    public List<StudentClassCheckDTO> getCheckInfoByMetaDataId(String studentClassCheckMetaDataId, LoginUser loginUser) {
        StudentClassCheckMetaData studentClassCheckMetaData = studentClassCheckMetaDataDao.findById(studentClassCheckMetaDataId).orElseThrow(() -> new NullFiledException("没有这个签到", HttpStatus.NOT_FOUND));
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        if (!studentClassCheckMetaData.getStudentClass().getUser().getId().equals(user.getId())) {
            throw new SecurityException("查询失败", HttpStatus.FORBIDDEN);
        }
        List<StudentClassUser> studentClassUserList = studentClassUserDao.findAllByStudentClass(studentClassCheckMetaData.getStudentClass());
        List<StudentClassCheck> studentClassCheckList = studentClassCheckDao.findAllByStudentClassCheckMetaData(studentClassCheckMetaData);
        return studentClassUserList
                .parallelStream()
                .map(studentClassUser -> {
                    StudentClassCheckDTO studentClassCheckDto = new StudentClassCheckDTO();
                    studentClassCheckDto.setUser(studentClassUser.getUser());
                    studentClassCheckDto.setStudentClass(studentClassUser.getStudentClass());
                    studentClassCheckDto.setCheck(false);
                    studentClassCheckDto.setCheckTime(null);
                    studentClassCheckDto.setGmtCreate(studentClassUser.getGmtCreate());
                    studentClassCheckDto.setGmtModified(studentClassUser.getGmtModified());
                    for (StudentClassCheck studentClassCheck : studentClassCheckList) {
                        if (studentClassUser.getUser().getId().equals(studentClassCheck.getUser().getId())) {
                            studentClassCheckDto.setCheck(true);
                            studentClassCheckDto.setCheckTime(studentClassCheck.getCheckTime());
                            studentClassCheckDto.setGmtCreate(studentClassCheck.getGmtCreate());
                            studentClassCheckDto.setGmtModified(studentClassCheck.getGmtModified());
                            break;
                        }
                    }
                    return studentClassCheckDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentClassCheckDTO> getUserCheckDetail(String studentUserName, String studentClassId, LoginUser loginUser) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        User studentUser = null;
        StudentClass studentClass = studentClassDao.findById(studentClassId).orElseThrow(() -> new NullFiledException("学生班级不存在", HttpStatus.NOT_FOUND));
        if (user.getUsername().equals(studentUserName)) {
            // 学生查询
            studentUser = user;
            if (!studentClassUserDao.existsByUserAndStudentClass(user, studentClass)) {
                // 该学生没有加入这个班级
                throw new SecurityException("查询失败", HttpStatus.FORBIDDEN);
            }
        } else if (!user.getId().equals(studentClass.getUser().getId())) {
            // 教师或辅导员登录 但是班级创建者和登录用户不一致
            throw new SecurityException("查询失败", HttpStatus.FORBIDDEN);
        }
        if (studentUser == null) {
            studentUser = infoClient.getUserInfoByUserName(studentUserName).orElseThrow(() -> new NullFiledException("学生不存在", HttpStatus.NOT_FOUND));
        }
        final User finalStudentUser = studentUser;
        return studentClassCheckMetaDataDao.findAllByStudentClass(studentClass)
                .stream()
                .sorted((o1, o2) -> {
                    if (o1.getGmtCreate().before(o2.getGmtCreate())) {
                        return 1;
                    } else if (o1.getGmtCreate().after(o2.getGmtCreate())) {
                        return -1;
                    } else {
                        return 0;
                    }
                })
                .map(studentClassCheckMetaData -> {
                    StudentClassCheckDTO studentClassCheckDto = new StudentClassCheckDTO();
                    studentClassCheckDto.setGmtCreate(studentClassCheckMetaData.getGmtCreate());
                    studentClassCheckDto.setGmtModified(studentClassCheckMetaData.getGmtModified());
                    StudentClassCheck studentClassCheck = studentClassCheckDao.findTopByUserAndStudentClassAndStudentClassCheckMetaData(finalStudentUser, studentClass, studentClassCheckMetaData);
                    if (studentClassCheck == null) {
                        studentClassCheckDto.setCheck(false);
                        studentClassCheckDto.setCheckTime(null);
                    } else {
                        studentClassCheckDto.setCheck(true);
                        studentClassCheckDto.setCheckTime(studentClassCheck.getCheckTime());
                        studentClassCheckDto.setGmtCreate(studentClassCheck.getGmtCreate());
                        studentClassCheckDto.setGmtModified(studentClassCheck.getGmtModified());
                    }
                    studentClassCheckDto.setUser(finalStudentUser);
                    studentClassCheckDto.setStudentClass(studentClass);
                    return studentClassCheckDto;
                })
                .collect(Collectors.toList());
    }
}
