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
import top.itning.smp.smpclass.client.entity.LeaveDTO;
import top.itning.smp.smpclass.dao.StudentClassCheckDao;
import top.itning.smp.smpclass.dao.StudentClassCheckMetaDataDao;
import top.itning.smp.smpclass.dao.StudentClassDao;
import top.itning.smp.smpclass.dao.StudentClassUserDao;
import top.itning.smp.smpclass.dto.StudentClassDTO;
import top.itning.smp.smpclass.entity.*;
import top.itning.smp.smpclass.exception.NullFiledException;
import top.itning.smp.smpclass.exception.SecurityException;
import top.itning.smp.smpclass.exception.UnexpectedException;
import top.itning.smp.smpclass.security.LoginUser;
import top.itning.smp.smpclass.service.ClassUserService;
import top.itning.smp.smpclass.util.DateUtils;
import top.itning.smp.smpclass.util.OrikaUtils;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final LeaveClient leaveClient;

    @Autowired
    public ClassUserServiceImpl(StudentClassUserDao studentClassUserDao, StudentClassDao studentClassDao, StudentClassCheckDao studentClassCheckDao, StudentClassCheckMetaDataDao studentClassCheckMetaDataDao, InfoClient infoClient, LeaveClient leaveClient) {
        this.studentClassUserDao = studentClassUserDao;
        this.studentClassDao = studentClassDao;
        this.studentClassCheckDao = studentClassCheckDao;
        this.studentClassCheckMetaDataDao = studentClassCheckMetaDataDao;
        this.infoClient = infoClient;
        this.leaveClient = leaveClient;
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

    @Override
    public Page<StudentClassDTO> getAllStudentClass(LoginUser loginUser, Pageable pageable) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        return studentClassDao.findByUser(user, pageable).map(studentClass -> {
            StudentClassDTO studentClassDTO = OrikaUtils.a2b(studentClass, StudentClassDTO.class);
            studentClassDTO.setStudentClassUserList(studentClassUserDao.findAllByStudentClass(studentClass));
            return studentClassDTO;
        });
    }

    @Override
    public Page<StudentClassCheckMetaData> getAllStudentClassCheckMetaData(String studentClassId, LoginUser loginUser, Pageable pageable) {
        StudentClass studentClass = getStudentClassAndCheckForbidden(studentClassId, loginUser);
        return studentClassCheckMetaDataDao.findAllByStudentClass(studentClass, pageable);
    }

    @Override
    public List<LeaveDTO> getStudentClassLeave(LoginUser loginUser, String studentClassId, Date whereDay) {
        StudentClass studentClass = getStudentClassAndCheckForbidden(studentClassId, loginUser);
        List<StudentClassUser> studentClassUserList = studentClassUserDao.findAllByStudentClass(studentClass);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return leaveClient.getAllLeave(DateUtils.date2LocalDateTime(whereDay).format(formatter))
                .stream()
                .filter(leaveDTO -> {
                    if (studentClassUserList == null || studentClassUserList.isEmpty()) {
                        return false;
                    }
                    return studentClassUserList.stream().anyMatch(studentClassUser -> studentClassUser.getUser().getId().equals(leaveDTO.getStudentUser().getId()));
                })
                .collect(Collectors.toList());
    }

    @Override
    public void delStudent(String studentUserName, String studentClassId, LoginUser loginUser) {
        User teacherUser = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        User studentUser = infoClient.getUserInfoByUserName(studentUserName).orElseThrow(() -> new NullFiledException("学生不存在", HttpStatus.NOT_FOUND));
        if (!studentUser.getRole().getId().equals(Role.STUDENT_ROLE_ID)) {
            // 不是学生
            throw new SecurityException("删除失败", HttpStatus.FORBIDDEN);
        }
        StudentClass studentClass = studentClassDao.findById(studentClassId).orElseThrow(() -> new NullFiledException("班级不存在", HttpStatus.NOT_FOUND));
        if (!studentClass.getUser().getId().equals(teacherUser.getId())) {
            // 不是这个教师的班级
            throw new SecurityException("删除失败", HttpStatus.FORBIDDEN);
        }
        StudentClassUser studentClassUser = studentClassUserDao.findByUserAndStudentClass(studentUser, studentClass);
        if (studentClassUser == null) {
            throw new NullFiledException("该班级没有这个学生", HttpStatus.BAD_REQUEST);
        }
        studentClassUserDao.delete(studentClassUser);
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

    /**
     * 获取学生班级并检查是否有横向越权
     *
     * @param studentClassId 学生班级ID
     * @param loginUser      登录用户
     * @return 学生班级
     */
    private StudentClass getStudentClassAndCheckForbidden(String studentClassId, LoginUser loginUser) {
        StudentClass studentClass = studentClassDao.findById(studentClassId).orElseThrow(() -> new NullFiledException("班级不存在", HttpStatus.NOT_FOUND));
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        if (!studentClass.getUser().getId().equals(user.getId())) {
            throw new SecurityException("查询失败", HttpStatus.FORBIDDEN);
        }
        return studentClass;
    }
}
