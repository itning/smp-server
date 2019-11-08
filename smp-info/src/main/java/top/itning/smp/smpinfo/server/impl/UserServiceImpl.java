package top.itning.smp.smpinfo.server.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smpinfo.dao.UserDao;
import top.itning.smp.smpinfo.entity.User;
import top.itning.smp.smpinfo.server.UserService;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Page<User> getAllUser(Pageable pageable) {
        return userDao.findAll(pageable);
    }
}
