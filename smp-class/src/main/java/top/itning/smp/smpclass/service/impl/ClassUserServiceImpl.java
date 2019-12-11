package top.itning.smp.smpclass.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smpclass.client.InfoClient;
import top.itning.smp.smpclass.client.LeaveClient;
import top.itning.smp.smpclass.client.entity.LeaveDTO;
import top.itning.smp.smpclass.dao.StudentClassCheckDao;
import top.itning.smp.smpclass.dao.StudentClassCheckMetaDataDao;
import top.itning.smp.smpclass.dao.StudentClassDao;
import top.itning.smp.smpclass.dao.StudentClassUserDao;
import top.itning.smp.smpclass.dto.StudentClassDTO;
import top.itning.smp.smpclass.entity.*;
import top.itning.smp.smpclass.exception.FileException;
import top.itning.smp.smpclass.exception.NullFiledException;
import top.itning.smp.smpclass.exception.SecurityException;
import top.itning.smp.smpclass.exception.UnexpectedException;
import top.itning.smp.smpclass.security.LoginUser;
import top.itning.smp.smpclass.service.ClassUserService;
import top.itning.smp.smpclass.util.DateUtils;
import top.itning.smp.smpclass.util.OrikaUtils;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    /**
     * XLS 文件扩展名
     */
    private static final String XLS_FILE = ".xls";
    /**
     * XLSX 文件扩展名
     */
    private static final String XLSX_FILE = ".xlsx";

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
                    return studentClassUserList.parallelStream().anyMatch(studentClassUser -> studentClassUser.getUser().getId().equals(leaveDTO.getStudentUser().getId()));
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

    @Override
    public List<StudentClassUser> importStudentByFile(MultipartFile file, String studentClassId, LoginUser loginUser) throws IOException {
        StudentClass studentClass = studentClassDao.findById(studentClassId).orElseThrow(() -> new NullFiledException("班级不存在", HttpStatus.NOT_FOUND));
        String originalFilename = file.getOriginalFilename();
        Workbook workbook;
        String fileType;
        if (StringUtils.isBlank(originalFilename)) {
            fileType = XLSX_FILE;
        } else {
            fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        if (XLS_FILE.equals(fileType)) {
            workbook = new HSSFWorkbook(file.getInputStream());
        } else if (XLSX_FILE.equals(fileType)) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else {
            throw new FileException("文件格式不正确", HttpStatus.BAD_REQUEST);
        }
        if (workbook.getNumberOfSheets() < 1) {
            throw new FileException("工作簿数量错误：" + workbook.getNumberOfSheets(), HttpStatus.BAD_REQUEST);
        }
        Sheet sheet = workbook.getSheetAt(0);
        List<StudentClassUser> studentClassUserList = new ArrayList<>();
        int lastRowNum = sheet.getLastRowNum();
        System.out.println(lastRowNum);
        for (int i = 0; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                String value = getCellValue(row, 0);
                if (value == null) {
                    continue;
                }
                if (value.length() > 3) {
                    value = value.substring(3).trim();
                }
                User user = infoClient.getStudentUserDtoByStudentId(value);
                if (user == null) {
                    continue;
                }
                if (studentClassUserDao.existsByUserAndStudentClass(user, studentClass)) {
                    continue;
                }
                StudentClassUser studentClassUser = new StudentClassUser();
                studentClassUser.setUser(user);
                studentClassUser.setStudentClass(studentClass);
                studentClassUserList.add(studentClassUserDao.save(studentClassUser));
            }
        }
        return studentClassUserList;
    }

    @Override
    public void delClassUserInfo(String counselorUsername, String studentUserName) {
        User counselorUser = infoClient.getUserInfoByUserName(counselorUsername).orElseThrow(() -> new UnexpectedException("用户不存在", HttpStatus.NOT_FOUND));
        StudentUser studentUser = infoClient.getStudentUserInfoByUserName(studentUserName).orElseThrow(() -> new UnexpectedException("学生不存在", HttpStatus.NOT_FOUND));
        if (!studentUser.getBelongCounselorId().equals(counselorUser.getId())) {
            throw new SecurityException("无法删除", HttpStatus.FORBIDDEN);
        }
        User user = new User();
        user.setId(studentUser.getId());
        studentClassCheckDao.deleteAllByUser(user);
        studentClassUserDao.deleteAllByUser(user);
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

    /**
     * 获取单元格中的数据
     *
     * @param row     行
     * @param cellNum 单元格号
     * @return 该单元格的数据
     */
    private String getCellValue(Row row, int cellNum) {
        String stringCellValue = null;
        Cell cell;
        if ((cell = row.getCell(cellNum)) != null) {
            try {
                stringCellValue = cell.getStringCellValue();
            } catch (IllegalStateException e) {
                logger.debug("getCellValue::CellNum->" + cellNum + "<-尝试获取String类型数据失败");
                try {
                    logger.debug("getCellValue::CellNum->" + cellNum + "<-尝试获取Double类型数据");
                    stringCellValue = String.valueOf(cell.getNumericCellValue());
                } catch (IllegalStateException e1) {
                    try {
                        logger.debug("getCellValue::CellNum->" + cellNum + "<-尝试获取Boolean类型数据");
                        stringCellValue = String.valueOf(cell.getBooleanCellValue());
                    } catch (IllegalStateException e2) {
                        try {
                            logger.debug("getCellValue::CellNum->" + cellNum + "<-尝试获取Date类型数据");
                            stringCellValue = String.valueOf(cell.getDateCellValue());
                        } catch (IllegalStateException e3) {
                            logger.warn("getCellValue::CellNum->" + cellNum + "<-未知类型数据->" + e3.getMessage());
                        }
                    }
                }
            } finally {
                logger.debug("getCellValue::第" + cellNum + "格获取到的数据为->" + stringCellValue);
            }
        } else {
            logger.debug("getCellValue::第" + cellNum + "格为空");
        }
        return stringCellValue;
    }
}
