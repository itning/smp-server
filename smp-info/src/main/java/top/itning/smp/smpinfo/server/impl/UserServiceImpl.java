package top.itning.smp.smpinfo.server.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smpinfo.dao.StudentUserDao;
import top.itning.smp.smpinfo.dao.UserDao;
import top.itning.smp.smpinfo.dto.StudentUserDTO;
import top.itning.smp.smpinfo.entity.StudentUser;
import top.itning.smp.smpinfo.entity.User;
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

    @Override
    public Page<StudentUserDTO> searchUsers(String key, Pageable pageable) {
        if (StringUtils.isNumeric(key)) {
            Page<StudentUser> studentUserPage = studentUserDao.findAll((Specification<StudentUser>) (root, query, criteriaBuilder) -> criteriaBuilder.and(criteriaBuilder.like(root.get("studentId"), "%" + key + "%")), pageable);
            return studentUserPage.map(studentUser -> {
                User user = userDao.findById(studentUser.getId()).orElse(null);
                return OrikaUtils.doubleEntity2Dto(user, studentUser, StudentUserDTO.class);
            });
        } else {
            Page<User> userPage = userDao.findAll((Specification<User>) (root, query, criteriaBuilder) -> criteriaBuilder.and(criteriaBuilder.like(root.get("name"), "%" + key + "%")), pageable);
            return userPage.map(user -> {
                StudentUser studentUser = studentUserDao.findById(user.getId()).orElse(null);
                return OrikaUtils.doubleEntity2Dto(user, studentUser, StudentUserDTO.class);
            });
        }
    }
}
