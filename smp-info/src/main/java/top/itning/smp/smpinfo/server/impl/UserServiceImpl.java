package top.itning.smp.smpinfo.server.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smpinfo.dao.StudentUserDao;
import top.itning.smp.smpinfo.dao.UserDao;
import top.itning.smp.smpinfo.dto.StudentUserDTO;
import top.itning.smp.smpinfo.entity.StudentUser;
import top.itning.smp.smpinfo.server.UserService;
import top.itning.smp.smpinfo.util.OrikaUtils;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    private final StudentUserDao studentUserDao;

    public UserServiceImpl(UserDao userDao, StudentUserDao studentUserDao) {
        this.userDao = userDao;
        this.studentUserDao = studentUserDao;
    }

    @Override
    public Page<StudentUserDTO> getAllUser(Pageable pageable) {
        return userDao.
                findAll(pageable).
                map(user -> {
                    StudentUser studentUser = studentUserDao.findById(user.getId()).orElse(null);
                    return OrikaUtils.doubleEntity2Dto(user, studentUser, StudentUserDTO.class);
                });
    }
}
