package top.itning.smp.smpinfo.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smpinfo.client.ClassClient;
import top.itning.smp.smpinfo.client.LeaveClient;
import top.itning.smp.smpinfo.client.RoomClient;
import top.itning.smp.smpinfo.dao.ApartmentDao;
import top.itning.smp.smpinfo.dao.RoleDao;
import top.itning.smp.smpinfo.dao.StudentUserDao;
import top.itning.smp.smpinfo.dao.UserDao;
import top.itning.smp.smpinfo.dto.StudentUserDTO;
import top.itning.smp.smpinfo.dto.UpFileDTO;
import top.itning.smp.smpinfo.entity.Role;
import top.itning.smp.smpinfo.entity.StudentUser;
import top.itning.smp.smpinfo.entity.User;
import top.itning.smp.smpinfo.exception.NullFiledException;
import top.itning.smp.smpinfo.exception.SecurityException;
import top.itning.smp.smpinfo.security.LoginUser;
import top.itning.smp.smpinfo.service.UserService;
import top.itning.smp.smpinfo.service.impl.excel.ExcelHandlerHelper;
import top.itning.smp.smpinfo.util.IdCardUtils;
import top.itning.smp.smpinfo.util.OrikaUtils;

import javax.persistence.criteria.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static top.itning.smp.smpinfo.entity.Role.*;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    private final StudentUserDao studentUserDao;

    private final ApartmentDao apartmentDao;

    private final LeaveClient leaveClient;

    private final ClassClient classClient;

    private final RoomClient roomClient;

    public UserServiceImpl(UserDao userDao, StudentUserDao studentUserDao, ApartmentDao apartmentDao, RoleDao roleDao, LeaveClient leaveClient, ClassClient classClient, RoomClient roomClient) {
        this.userDao = userDao;
        this.studentUserDao = studentUserDao;
        this.apartmentDao = apartmentDao;
        this.leaveClient = leaveClient;
        this.classClient = classClient;
        this.roomClient = roomClient;
        checkAndInitRole(roleDao);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void checkAndInitRole(RoleDao roleDao) {
        if (roleDao.existsById(STUDENT_ROLE_ID)) {
            Role role = roleDao.findById(STUDENT_ROLE_ID).get();
            if (!STUDENT_ROLE_ID_STR.equals(role.getName())) {
                logger.error("Id 1 Role Not Student And Is {}", role.getName());
            }
        } else {
            Role role = new Role();
            role.setId(STUDENT_ROLE_ID);
            role.setName(STUDENT_ROLE_ID_STR);
            roleDao.saveAndFlush(role);
        }
        if (roleDao.existsById(TEACHER_ROLE_ID)) {
            Role role = roleDao.findById(TEACHER_ROLE_ID).get();
            if (!TEACHER_ROLE_ID_STR.equals(role.getName())) {
                logger.error("Id 2 Role Not Teacher And Is {}", role.getName());
            }
        } else {
            Role role = new Role();
            role.setId(TEACHER_ROLE_ID);
            role.setName(TEACHER_ROLE_ID_STR);
            roleDao.saveAndFlush(role);
        }
        if (roleDao.existsById(COUNSELOR_ROLE_ID)) {
            Role role = roleDao.findById(COUNSELOR_ROLE_ID).get();
            if (!COUNSELOR_ROLE_ID_STR.equals(role.getName())) {
                logger.error("Id 3 Role Not Counselor And Is {}", role.getName());
            }
        } else {
            Role role = new Role();
            role.setId(COUNSELOR_ROLE_ID);
            role.setName(COUNSELOR_ROLE_ID_STR);
            roleDao.saveAndFlush(role);
        }
    }

    @Override
    public Page<StudentUserDTO> getAllUser(Pageable pageable, LoginUser loginUser) {
        User savedUser = userDao.findByUsername(loginUser.getUsername());
        Sort.Order order = pageable.getSort().toList().get(0);
        Page<StudentUserDTO> page = userDao.findAll((Specification<User>) (root, query, cb) -> {
            Join<User, StudentUser> studentUserJoin = root.join("studentUser", JoinType.INNER);
            order(query, cb, order, root, studentUserJoin, "gmtModified", "name", "tel", "email");
            return cb.and(
                    cb.equal(root.get("role"), Role.withStudentUser()),
                    cb.equal(studentUserJoin.get("belongCounselorId"), savedUser.getId())
            );
        }, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()))
                .map(user -> {
                    StudentUser studentUser = studentUserDao.findById(user.getId()).orElse(null);
                    return OrikaUtils.doubleEntity2Dto(user, studentUser, StudentUserDTO.class);
                });
        return new PageImpl<>(page.getContent(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()), page.getTotalElements());
    }

    @Override
    public Page<StudentUserDTO> searchUsers(String key, Pageable pageable, LoginUser loginUser) {
        User savedUser = userDao.findByUsername(loginUser.getUsername());
        Sort.Order order = pageable.getSort().toList().get(0);
        Page<StudentUserDTO> userPage = userDao.findAll((Specification<User>) (root, query, cb) -> {
                    List<Predicate> list = new ArrayList<>();
                    Join<User, StudentUser> studentUserJoin = root.join("studentUser", JoinType.INNER);

                    if (StringUtils.isNumeric(key)) {
                        list.add(cb.like(studentUserJoin.get("studentId"), "%" + key + "%"));
                    } else {
                        list.add(cb.like(root.get("name"), "%" + key + "%"));
                    }
                    list.add(cb.equal(root.get("role"), Role.withStudentUser()));
                    list.add(cb.equal(studentUserJoin.get("belongCounselorId"), savedUser.getId()));

                    order(query, cb, order, root, studentUserJoin, "gmtModified", "name", "tel", "email");

                    Predicate[] p = new Predicate[list.size()];
                    return cb.and(list.toArray(p));
                },
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())).map(user -> {
            StudentUser studentUser = studentUserDao.findById(user.getId()).orElse(null);
            return OrikaUtils.doubleEntity2Dto(user, studentUser, StudentUserDTO.class);
        });
        return new PageImpl<>(userPage.getContent(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()), userPage.getTotalElements());
    }

    @Override
    public void updateUser(StudentUserDTO studentUserDTO, LoginUser loginUser) {
        if (studentUserDTO == null || StringUtils.isBlank(studentUserDTO.getId())) {
            throw new NullFiledException("ID为空", HttpStatus.BAD_REQUEST);
        }
        if (studentUserDTO.getIdCard() != null) {
            Map<String, Object> map = IdCardUtils.analysisIdCard(studentUserDTO.getIdCard());
            studentUserDTO.setAge((Integer) map.get("age"));
            studentUserDTO.setBirthday((Date) map.get("birthday"));
            studentUserDTO.setSex((Boolean) map.get("sex"));
        }
        StudentUser savedStudentUser = studentUserDao
                .findById(studentUserDTO.getId())
                .orElseThrow(() -> new NullFiledException("学生不存在", HttpStatus.BAD_REQUEST));
        User loginUserEntity = userDao.findByUsername(loginUser.getUsername());
        if (!loginUserEntity.getId().equals(savedStudentUser.getBelongCounselorId())) {
            // 不是这个辅导员的学生
            throw new SecurityException("更新失败", HttpStatus.FORBIDDEN);
        }
        User savedUser = userDao
                .findById(studentUserDTO.getId())
                .orElseThrow(() -> new NullFiledException("学生不存在", HttpStatus.BAD_REQUEST));

        StudentUser studentUser = OrikaUtils.a2b(studentUserDTO, StudentUser.class);
        User user = OrikaUtils.a2b(studentUserDTO, User.class);

        try {
            Field[] declaredFields = studentUser.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                if (field.get(studentUser) != null) {
                    field.set(savedStudentUser, field.get(studentUser));
                }
            }

            Field[] fields = user.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.get(user) != null) {
                    field.set(savedUser, field.get(user));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }

        studentUserDao.save(savedStudentUser);
        userDao.save(savedUser);
    }

    @Override
    public void delUser(String userId, LoginUser loginUser) {
        Optional<StudentUser> studentUserOptional = studentUserDao.findById(userId);
        Optional<User> studentUser = userDao.findById(userId);
        if (StringUtils.isBlank(userId) || !studentUser.isPresent() || !studentUserOptional.isPresent()) {
            throw new NullFiledException("学生不存在", HttpStatus.BAD_REQUEST);
        }
        User loginUserEntity = userDao.findByUsername(loginUser.getUsername());
        if (!loginUserEntity.getId().equals(studentUserOptional.get().getBelongCounselorId())) {
            // 不是这个辅导员的学生
            throw new SecurityException("删除失败", HttpStatus.FORBIDDEN);
        }
        User user = studentUser.get();
        classClient.delClassUserInfo(loginUser.getUsername(), user.getUsername());
        leaveClient.delLeaveInfo(loginUser.getUsername(), user.getUsername());
        roomClient.delRoomInfo(loginUser.getUsername(), user.getUsername());
        studentUserDao.delete(studentUserOptional.get());
        userDao.deleteById(userId);
    }

    @Override
    public UpFileDTO upFile(MultipartFile file, LoginUser loginUser) throws IOException {
        return new ExcelHandlerHelper(userDao, studentUserDao, apartmentDao).readAndSave(file, loginUser);
    }

    @Override
    public User getUserInfoByUserName(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public StudentUserDTO getStudentUserInfoByUserName(String username) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            return null;
        }
        Optional<StudentUser> studentUserOptional = studentUserDao.findById(user.getId());
        if (!studentUserOptional.isPresent()) {
            return null;
        }
        StudentUser studentUser = studentUserOptional.get();
        return OrikaUtils.doubleEntity2Dto(user, studentUser, StudentUserDTO.class);
    }

    @Override
    public long countStudent(String username) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new NullFiledException("用户不存在", HttpStatus.NOT_FOUND);
        }
        return studentUserDao.countAllByBelongCounselorId(user.getId());
    }

    @Override
    public List<StudentUserDTO> getAllUser(String username) {
        User daoByUsername = userDao.findByUsername(username);
        return studentUserDao.findAllByBelongCounselorId(daoByUsername.getId()).stream()
                .map(studentUser -> {
                    User user = userDao.findById(studentUser.getId()).orElse(null);
                    return OrikaUtils.doubleEntity2Dto(user, studentUser, StudentUserDTO.class);
                })
                .collect(Collectors.toList());
    }

    @Override
    public StudentUserDTO getStudentUserDtoByStudentId(String studentId) {
        StudentUser studentUser = studentUserDao.findByStudentId(studentId);
        if (studentUser == null) {
            return null;
        }
        User user = userDao.findById(studentUser.getId()).orElse(null);
        return OrikaUtils.doubleEntity2Dto(user, studentUser, StudentUserDTO.class);
    }

    @Override
    public List<User> getAllCounselorUser() {
        Role role = new Role();
        role.setId(COUNSELOR_ROLE_ID);
        return userDao.findByRole(role);
    }

    @Override
    public boolean changeUserPwd(String username, String newPwd) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            return false;
        } else {
            user.setPassword(newPwd);
            User saved = userDao.save(user);
            return saved.getPassword().equals(newPwd);
        }
    }

    /**
     * 排序
     *
     * @param query     CriteriaQuery
     * @param cb        CriteriaBuilder
     * @param order     Sort.Order
     * @param root      Root
     * @param join      Join
     * @param noJoinStr 不需要JOIN的字段
     * @param <A>       A 实体
     * @param <B>       B 实体
     */
    private <A, B> void order(CriteriaQuery<?> query, CriteriaBuilder cb, Sort.Order order, Root<A> root, Join<A, B> join, String... noJoinStr) {
        boolean noJoin = false;
        for (String s : noJoinStr) {
            if (s.equals(order.getProperty())) {
                noJoin = true;
                break;
            }
        }
        if (noJoin) {
            if (order.isDescending()) {
                query.orderBy(cb.desc(root.get(order.getProperty())));
            } else {
                query.orderBy(cb.asc(root.get(order.getProperty())));
            }
        } else {
            if (order.isDescending()) {
                query.orderBy(cb.desc(join.get(order.getProperty())));
            } else {
                query.orderBy(cb.asc(join.get(order.getProperty())));
            }
        }
    }
}
