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
import top.itning.smp.smpclass.dao.StudentClassDao;
import top.itning.smp.smpclass.dao.StudentClassUserDao;
import top.itning.smp.smpclass.entity.StudentClass;
import top.itning.smp.smpclass.entity.StudentClassUser;
import top.itning.smp.smpclass.entity.User;
import top.itning.smp.smpclass.exception.NullFiledException;
import top.itning.smp.smpclass.exception.UnexpectedException;
import top.itning.smp.smpclass.security.LoginUser;
import top.itning.smp.smpclass.service.ClassUserService;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClassUserServiceImpl implements ClassUserService {
    private static final Logger logger = LoggerFactory.getLogger(ClassUserServiceImpl.class);
    private final StudentClassUserDao studentClassUserDao;
    private final StudentClassDao studentClassDao;
    private final InfoClient infoClient;

    @Autowired
    public ClassUserServiceImpl(StudentClassUserDao studentClassUserDao, StudentClassDao studentClassDao, InfoClient infoClient) {
        this.studentClassUserDao = studentClassUserDao;
        this.studentClassDao = studentClassDao;
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
}
