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
import top.itning.smp.smpclass.dao.StudentClassUserDao;
import top.itning.smp.smpclass.entity.StudentClass;
import top.itning.smp.smpclass.entity.StudentClassUser;
import top.itning.smp.smpclass.entity.User;
import top.itning.smp.smpclass.exception.NullFiledException;
import top.itning.smp.smpclass.exception.SecurityException;
import top.itning.smp.smpclass.exception.UnexpectedException;
import top.itning.smp.smpclass.security.LoginUser;
import top.itning.smp.smpclass.service.ClassUserService;

import java.util.UUID;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClassUserServiceImpl implements ClassUserService {
    private static final Logger logger = LoggerFactory.getLogger(ClassUserServiceImpl.class);
    private final StudentClassUserDao studentClassUserDao;
    private final StudentClassDao studentClassDao;
    private final StudentClassCheckDao studentClassCheckDao;
    private final StudentClassCheckMetaDataDao studentClassCheckMetaDataDao;
    private final InfoClient infoClient;

    @Autowired
    public ClassUserServiceImpl(StudentClassUserDao studentClassUserDao, StudentClassDao studentClassDao, StudentClassCheckDao studentClassCheckDao, StudentClassCheckMetaDataDao studentClassCheckMetaDataDao, InfoClient infoClient) {
        this.studentClassUserDao = studentClassUserDao;
        this.studentClassDao = studentClassDao;
        this.studentClassCheckDao = studentClassCheckDao;
        this.studentClassCheckMetaDataDao = studentClassCheckMetaDataDao;
        this.infoClient = infoClient;
    }

    @Override
    public Page<StudentClassUser> getAllStudentClassUsers(LoginUser loginUser, Pageable pageable) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        return studentClassUserDao.findAllByUser(user, pageable);
    }

    @Override
    public StudentClassUser joinClass(LoginUser loginUser, String classNum) {
        StudentClass studentClass = studentClassDao.findByClassNum(classNum).orElseThrow(() -> new NullFiledException("班级不存在", HttpStatus.NOT_FOUND));
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        if (studentClassUserDao.existsByUserAndStudentClass(user, studentClass)) {
            throw new NullFiledException("已经在这个班级了", HttpStatus.BAD_REQUEST);
        }
        StudentClassUser studentClassUser = new StudentClassUser();
        studentClassUser.setUser(user);
        studentClassUser.setStudentClass(studentClass);
        return studentClassUserDao.save(studentClassUser);
    }

    @Override
    public StudentClass newClass(LoginUser loginUser, String name) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        String classNum = generateClassNum(user, name);
        StudentClass studentClass = new StudentClass();
        studentClass.setName(name);
        studentClass.setClassNum(classNum);
        studentClass.setUser(user);
        return studentClassDao.save(studentClass);
    }

    @Override
    public void quitClass(LoginUser loginUser, String studentClassId) {
        StudentClass studentClass = studentClassDao.findById(studentClassId).orElseThrow(() -> new NullFiledException("班级不存在", HttpStatus.NOT_FOUND));
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        StudentClassUser studentClassUser = studentClassUserDao.findByUserAndStudentClass(user, studentClass);
        if (studentClassUser == null) {
            throw new NullFiledException("班级不存在", HttpStatus.NOT_FOUND);
        }
        if (!studentClassUser.getUser().getId().equals(user.getId())) {
            throw new SecurityException("退出失败", HttpStatus.FORBIDDEN);
        }
        // 不需要删除用户打卡数据，在教师解散班级时删除
        studentClassUserDao.delete(studentClassUser);
    }

    @Override
    public void delClass(LoginUser loginUser, String studentClassId) {
        StudentClass studentClass = studentClassDao.findById(studentClassId).orElseThrow(() -> new NullFiledException("班级不存在", HttpStatus.NOT_FOUND));
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        if (!studentClass.getUser().getId().equals(user.getId())) {
            throw new SecurityException("删除失败", HttpStatus.FORBIDDEN);
        }
        // 1.删除打卡数据
        studentClassCheckDao.deleteAllByStudentClass(studentClass);
        // 2.删除元数据
        studentClassCheckMetaDataDao.deleteAllByStudentClass(studentClass);
        // 3.删除所有学生班级
        studentClassUserDao.deleteAllByStudentClass(studentClass);
        // 4.删除班级
        studentClassDao.deleteById(studentClassId);
    }

    /**
     * 生成班号
     *
     * @param user         用户
     * @param newClassName 班名
     * @return 班号
     */
    private String generateClassNum(User user, String newClassName) {
        String classNum = UUID.nameUUIDFromBytes((newClassName + user.getName() + System.currentTimeMillis()).getBytes()).toString().substring(0, 8);
        int count = 0;
        while (studentClassDao.findByClassNum(classNum).isPresent()) {
            classNum = UUID.nameUUIDFromBytes((newClassName + user.getName() + System.currentTimeMillis()).getBytes()).toString().substring(0, 8);
            count++;
            if (count >= 3) {
                // 倒霉孩子
                classNum = UUID.randomUUID().toString().substring(0, 8) + String.valueOf(System.currentTimeMillis()).substring(7);
                break;
            }
        }
        return classNum.toUpperCase();
    }
}
