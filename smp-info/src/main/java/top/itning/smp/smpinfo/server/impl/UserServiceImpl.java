package top.itning.smp.smpinfo.server.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smpinfo.dao.ApartmentDao;
import top.itning.smp.smpinfo.dao.StudentUserDao;
import top.itning.smp.smpinfo.dao.UserDao;
import top.itning.smp.smpinfo.dto.StudentUserDTO;
import top.itning.smp.smpinfo.dto.UpFileDTO;
import top.itning.smp.smpinfo.entity.Apartment;
import top.itning.smp.smpinfo.entity.Role;
import top.itning.smp.smpinfo.entity.StudentUser;
import top.itning.smp.smpinfo.entity.User;
import top.itning.smp.smpinfo.exception.FileException;
import top.itning.smp.smpinfo.exception.NullFiledException;
import top.itning.smp.smpinfo.server.UserService;
import top.itning.smp.smpinfo.util.IdCardUtils;
import top.itning.smp.smpinfo.util.OrikaUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    /**
     * XLS 文件扩展名
     */
    private static final String XLS_FILE = ".xls";
    /**
     * XLSX 文件扩展名
     */
    private static final String XLSX_FILE = ".xlsx";
    /**
     * 邮箱正则
     */
    @SuppressWarnings("all")
    private static final String EMAIL_REGULAR = "^([A-Za-z0-9_\\-\\.\\u4e00-\\u9fa5])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,8})$";
    /**
     * 民族数据
     */
    private static final String[] ETHNIC_RANGE_DATA = new String[]{"汉", "蒙古", "回", "藏", "维吾尔", "苗", "彝", "壮", "布依", "朝鲜", "满", "侗", "瑶", "白", "土家",
            "哈尼", "哈萨克", "傣", "黎", "傈僳", "佤", "畲", "高山", "拉祜", "水", "东乡", "纳西", "景颇", "柯尔克孜",
            "土", "达斡尔", "仫佬", "羌", "布朗", "撒拉", "毛南", "仡佬", "锡伯", "阿昌", "普米", "塔吉克", "怒", "乌孜别克",
            "俄罗斯", "鄂温克", "德昂", "保安", "裕固", "京", "塔塔尔", "独龙", "鄂伦春", "赫哲", "门巴", "珞巴", "基诺"};
    /**
     * 政治面貌数据
     */
    private static final String[] POLITICAL_RANGE_DATA = new String[]{"中共党员", "中共预备党员", "共青团员", "民革党员", "民盟盟员", "民建会员", "民进会员", "农工党党员", "致公党党员", "九三学社社员", "台盟盟员", "无党派人士", "群众"};

    private final UserDao userDao;

    private final StudentUserDao studentUserDao;

    private final ApartmentDao apartmentDao;

    public UserServiceImpl(UserDao userDao, StudentUserDao studentUserDao, ApartmentDao apartmentDao) {
        this.userDao = userDao;
        this.studentUserDao = studentUserDao;
        this.apartmentDao = apartmentDao;
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

    @Override
    public void updateUser(StudentUserDTO studentUserDTO) {
        if (StringUtils.isBlank(studentUserDTO.getId())) {
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
    public void delUser(String userId) {
        if (!userDao.existsById(userId) || !studentUserDao.existsById(userId)) {
            throw new NullFiledException("学生不存在", HttpStatus.BAD_REQUEST);
        }
        studentUserDao.deleteById(userId);
        userDao.deleteById(userId);
    }

    @Override
    public UpFileDTO upFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        Workbook workbook;
        String fileType;
        if (originalFilename == null) {
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
        Role role = new Role();
        role.setId("1");
        Set<String> set = new TreeSet<>();
        List<StudentUserDTO> studentUserDtoList = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 1; i <= lastRowNum; i++) {
            boolean canSave = true;
            Row row = sheet.getRow(i);

            String name = getCellValue(row, 0);
            String tel = getCellValue(row, 1);
            String email = getCellValue(row, 2);
            String studentId = getCellValue(row, 3);
            String idCard = getCellValue(row, 4);
            String politicalStatus = getCellValue(row, 5);
            String ethnic = getCellValue(row, 6);
            String apartmentName = getCellValue(row, 7);
            String roomNum = getCellValue(row, 8);
            String bedNum = getCellValue(row, 9);
            String address = getCellValue(row, 10);

            if (StringUtils.isAllBlank(name, tel, email, studentId, idCard, politicalStatus, ethnic, apartmentName, roomNum, bedNum, address)) {
                logger.warn("已跳过第" + (i + 1) + "行");
                continue;
            }

            Apartment apartment = apartmentDao.findByName(apartmentName);

            canSave = check(set, i, canSave, tel, email, studentId, apartmentName, apartment, politicalStatus, ethnic, roomNum, bedNum);

            Map<String, Object> map = null;
            try {
                map = IdCardUtils.analysisIdCard(idCard);
            } catch (Exception e) {
                canSave = false;
                set.add(initErrorMsg(i, 5, "身份证号%s错误：%s", idCard, e.getMessage()));
            }

            if (canSave) {
                StudentUserDTO studentUserDTO = new StudentUserDTO();
                studentUserDTO.setName(name);
                studentUserDTO.setTel(tel);
                studentUserDTO.setEmail(email);
                studentUserDTO.setUsername(studentId);
                studentUserDTO.setRole(role);
                studentUserDTO.setBirthday((Date) map.get("birthday"));
                studentUserDTO.setSex((Boolean) map.get("sex"));
                studentUserDTO.setAge((Integer) map.get("age"));
                studentUserDTO.setStudentId(studentId);
                studentUserDTO.setIdCard(idCard);
                studentUserDTO.setPoliticalStatus(politicalStatus);
                studentUserDTO.setEthnic(ethnic);

                studentUserDTO.setApartment(apartment);
                studentUserDTO.setRoomNum(roomNum);
                studentUserDTO.setBedNum(bedNum);
                studentUserDTO.setAddress(address);
                Date date = new Date();
                studentUserDTO.setGmtCreate(date);
                studentUserDTO.setGmtModified(date);

                studentUserDtoList.add(studentUserDTO);
            }
        }
        if (set.isEmpty()) {
            studentUserDtoList.forEach(studentUserDTO -> {
                User user = OrikaUtils.a2b(studentUserDTO, User.class);
                user.setPassword(studentUserDTO.getName());
                User savedUser = userDao.save(user);
                StudentUser studentUser = OrikaUtils.a2b(studentUserDTO, StudentUser.class);
                studentUser.setId(savedUser.getId());
                studentUserDao.save(studentUser);
            });
            long count = studentUserDao.count();
            return new UpFileDTO((long) studentUserDtoList.size(), count, null);
        } else {
            return new UpFileDTO(null, null, set);
        }
    }

    /**
     * 检查值
     */
    private boolean check(Set<String> set, int i, boolean canSave,
                          String tel,
                          String email,
                          String studentId,
                          String apartmentName,
                          Apartment apartment,
                          String politicalStatus,
                          String ethnic,
                          String roomNum,
                          String bedNum) {
        if (StringUtils.isBlank(tel) || tel.trim().length() != 11) {
            canSave = false;
            set.add(initErrorMsg(i, 2, "手机号%s长度错误", tel));
        }
        if (!Pattern.matches(EMAIL_REGULAR, email)) {
            canSave = false;
            set.add(initErrorMsg(i, 3, "邮箱%s格式不正确", email));
        }
        if (studentUserDao.existsByStudentId(studentId)) {
            canSave = false;
            set.add(initErrorMsg(i, 4, "学号%s重复，数据库已有该学号", studentId));
        }
        if (Arrays.stream(POLITICAL_RANGE_DATA).noneMatch(s -> s.equals(politicalStatus))) {
            canSave = false;
            set.add(initErrorMsg(i, 6, "政治面貌%s错误", politicalStatus));
        }
        if (Arrays.stream(ETHNIC_RANGE_DATA).noneMatch(s -> s.equals(ethnic))) {
            canSave = false;
            set.add(initErrorMsg(i, 7, "民族%s错误", ethnic));
        }
        if (apartment == null || StringUtils.isBlank(apartment.getId())) {
            canSave = false;
            set.add(initErrorMsg(i, 8, "公寓名%s没有找到", apartmentName));
        } else {
            StudentUser studentUser = studentUserDao.findByApartmentAndRoomNumAndBedNum(apartment, roomNum, bedNum);
            if (studentUser != null) {
                canSave = false;
                set.add(initErrorMsg(i, 10, "%s %s寝室%s床铺 已经有同学(学号：%s)在住了", apartmentName, roomNum, bedNum, studentUser.getStudentId()));
            }
        }
        return canSave;
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

    /**
     * 初始化错误消息
     *
     * @param rowNum  行号
     * @param cellNum 列号
     * @param format  格式化
     * @param args    参数
     * @return 错误消息
     */
    private String initErrorMsg(int rowNum, int cellNum, String format, Object... args) {
        String info = "在第" + (rowNum + 1) + "行" + "第" + cellNum + "列";
        return String.format(info + format, args);
    }
}
