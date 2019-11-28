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
import top.itning.smp.smpclass.dao.StudentClassUserDao;
import top.itning.smp.smpclass.entity.StudentClassUser;
import top.itning.smp.smpclass.entity.User;
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
    private final StudentClassUserDao studentGroupDao;
    private final InfoClient infoClient;

    @Autowired
    public ClassUserServiceImpl(StudentClassUserDao studentGroupDao, InfoClient infoClient) {
        this.studentGroupDao = studentGroupDao;
        this.infoClient = infoClient;
    }

    @Override
    public Page<StudentClassUser> getAllStudentClassUsers(LoginUser loginUser, Pageable pageable) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        return studentGroupDao.findAllByUser(user, pageable);
    }
}
