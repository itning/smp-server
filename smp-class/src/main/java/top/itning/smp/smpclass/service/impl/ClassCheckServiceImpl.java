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
import top.itning.smp.smpclass.dao.StudentClassCheckDao;
import top.itning.smp.smpclass.dao.StudentClassCheckMetaDataDao;
import top.itning.smp.smpclass.dao.StudentClassDao;
import top.itning.smp.smpclass.entity.StudentClass;
import top.itning.smp.smpclass.entity.StudentClassCheck;
import top.itning.smp.smpclass.entity.StudentClassCheckMetaData;
import top.itning.smp.smpclass.entity.User;
import top.itning.smp.smpclass.exception.NullFiledException;
import top.itning.smp.smpclass.exception.UnexpectedException;
import top.itning.smp.smpclass.security.LoginUser;
import top.itning.smp.smpclass.service.ClassCheckService;
import top.itning.smp.smpclass.util.GpsUtils;

import java.util.Date;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClassCheckServiceImpl implements ClassCheckService {
    private static final Logger logger = LoggerFactory.getLogger(ClassCheckServiceImpl.class);
    private final StudentClassCheckMetaDataDao studentClassCheckMetaDataDao;
    private final StudentClassCheckDao studentClassCheckDao;
    private final StudentClassDao studentClassDao;
    private final InfoClient infoClient;

    @Autowired
    public ClassCheckServiceImpl(StudentClassCheckMetaDataDao studentClassCheckMetaDataDao, StudentClassCheckDao studentClassCheckDao, StudentClassDao studentClassDao, InfoClient infoClient) {
        this.studentClassCheckMetaDataDao = studentClassCheckMetaDataDao;
        this.studentClassCheckDao = studentClassCheckDao;
        this.studentClassDao = studentClassDao;
        this.infoClient = infoClient;
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
}
