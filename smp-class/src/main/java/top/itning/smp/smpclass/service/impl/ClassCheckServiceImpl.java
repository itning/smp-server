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
import top.itning.smp.smpclass.dao.StudentClassDao;
import top.itning.smp.smpclass.entity.StudentClass;
import top.itning.smp.smpclass.entity.StudentClassCheck;
import top.itning.smp.smpclass.entity.User;
import top.itning.smp.smpclass.exception.NullFiledException;
import top.itning.smp.smpclass.exception.UnexpectedException;
import top.itning.smp.smpclass.security.LoginUser;
import top.itning.smp.smpclass.service.ClassCheckService;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClassCheckServiceImpl implements ClassCheckService {
    private static final Logger logger = LoggerFactory.getLogger(ClassCheckServiceImpl.class);
    private final StudentClassCheckDao studentClassCheckDao;
    private final StudentClassDao studentClassDao;
    private final InfoClient infoClient;

    @Autowired
    public ClassCheckServiceImpl(StudentClassCheckDao studentClassCheckDao, StudentClassDao studentClassDao, InfoClient infoClient) {
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
}
