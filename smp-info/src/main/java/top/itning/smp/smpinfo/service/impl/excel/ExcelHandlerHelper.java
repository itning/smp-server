package top.itning.smp.smpinfo.service.impl.excel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smpinfo.dao.ApartmentDao;
import top.itning.smp.smpinfo.dao.StudentUserDao;
import top.itning.smp.smpinfo.dao.UserDao;
import top.itning.smp.smpinfo.dto.StudentUserDTO;
import top.itning.smp.smpinfo.entity.Apartment;
import top.itning.smp.smpinfo.entity.StudentUser;
import top.itning.smp.smpinfo.security.LoginUser;
import top.itning.smp.smpinfo.util.IdCardUtils;
import top.itning.utils.tuple.Tuple2;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <P>新增学生信息帮助类
 * <P>实例化该类并调用父类{@link AbstractReadExcel2Save#readAndSave(MultipartFile, LoginUser)}方法
 * <P>用户名密码生成规则见{@link #getUserNameAndPassword(StudentUserDTO)}
 *
 * @author itning
 * @date 2020/1/21 19:45
 * @see top.itning.smp.smpinfo.service.impl.excel.AbstractExcelHandlerHelper
 * @see top.itning.smp.smpinfo.service.impl.excel.AbstractReadExcel2Save
 */
public final class ExcelHandlerHelper extends AbstractExcelHandlerHelper {
    /**
     * 手机号长度
     */
    private static final int TEL_LENGTH = 11;
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

    private final StudentUserDao studentUserDao;
    private final ApartmentDao apartmentDao;
    private final ThreadLocal<Apartment> apartmentThreadLocal = new ThreadLocal<>();

    public ExcelHandlerHelper(UserDao userDao, StudentUserDao studentUserDao, ApartmentDao apartmentDao) {
        super(userDao, studentUserDao);
        this.studentUserDao = studentUserDao;
        this.apartmentDao = apartmentDao;
    }

    private Apartment getApartment(String apartmentName) {
        if (apartmentThreadLocal.get() == null) {
            Apartment find = apartmentDao.findByName(apartmentName);
            if (find == null) {
                return null;
            }
            apartmentThreadLocal.set(find);
        }
        return apartmentThreadLocal.get();
    }

    @Override
    protected Map<String, Object> analysisIdCard(String idCard, int rowIndex) {
        try {
            return IdCardUtils.analysisIdCard(idCard);
        } catch (Exception e) {
            super.errorInfoSet.add(initErrorMsg(rowIndex, 5, "身份证号%s错误：%s", idCard, e.getMessage()));
            return null;
        }
    }

    @Override
    protected boolean checkExcelData(ExcelData excelData, int rowIndex) {
        boolean canSave = true;
        if (StringUtils.isBlank(excelData.getTel()) || excelData.getTel().trim().length() != TEL_LENGTH) {
            canSave = false;
            super.errorInfoSet.add(initErrorMsg(rowIndex, 2, "手机号%s长度错误", excelData.getTel()));
        }
        if (excelData.getEmail() == null || !Pattern.matches(EMAIL_REGULAR, excelData.getEmail())) {
            canSave = false;
            super.errorInfoSet.add(initErrorMsg(rowIndex, 3, "邮箱%s格式不正确", excelData.getEmail()));
        }
        if (studentUserDao.existsByStudentId(excelData.getStudentId())) {
            canSave = false;
            super.errorInfoSet.add(initErrorMsg(rowIndex, 4, "学号%s重复，数据库已有该学号", excelData.getStudentId()));
        }
        if (Arrays.stream(POLITICAL_RANGE_DATA).noneMatch(s -> s.equals(excelData.getPoliticalStatus()))) {
            canSave = false;
            super.errorInfoSet.add(initErrorMsg(rowIndex, 6, "政治面貌%s错误", excelData.getPoliticalStatus()));
        }
        if (Arrays.stream(ETHNIC_RANGE_DATA).noneMatch(s -> s.equals(excelData.getEthnic()))) {
            canSave = false;
            super.errorInfoSet.add(initErrorMsg(rowIndex, 7, "民族%s错误", excelData.getEthnic()));
        }
        Apartment apartment = getApartment(excelData.getApartmentName());
        if (apartment == null || StringUtils.isBlank(apartment.getId())) {
            canSave = false;
            super.errorInfoSet.add(initErrorMsg(rowIndex, 8, "公寓名%s没有找到", excelData.getApartmentName()));
        } else {
            StudentUser studentUser = studentUserDao.findByApartmentAndRoomNumAndBedNum(apartment, excelData.getRoomNum(), excelData.getBedNum());
            if (studentUser != null) {
                canSave = false;
                super.errorInfoSet.add(initErrorMsg(rowIndex, 10, "%s %s寝室%s床铺 已经有同学(学号：%s)在住了", excelData.getApartmentName(), excelData.getRoomNum(), excelData.getBedNum(), studentUser.getStudentId()));
            }
        }
        return canSave;
    }

    @Override
    protected StudentUserDTO generateStudentUserDto(ExcelData excelData, IdCardData idCardData) {
        StudentUserDTO studentUserDTO = new StudentUserDTO();
        studentUserDTO.setName(excelData.getName());
        studentUserDTO.setTel(excelData.getTel());
        studentUserDTO.setEmail(excelData.getEmail());
        studentUserDTO.setRole(super.studentRole);
        studentUserDTO.setBirthday(idCardData.getBirthday());
        studentUserDTO.setSex(idCardData.isSex());
        studentUserDTO.setAge(idCardData.getAge());
        studentUserDTO.setStudentId(excelData.getStudentId());
        studentUserDTO.setIdCard(excelData.getIdCard());
        studentUserDTO.setPoliticalStatus(excelData.getPoliticalStatus());
        studentUserDTO.setEthnic(excelData.getEthnic());
        studentUserDTO.setApartment(getApartment(excelData.getApartmentName()));
        studentUserDTO.setRoomNum(excelData.getRoomNum());
        studentUserDTO.setBedNum(excelData.getBedNum());
        studentUserDTO.setAddress(excelData.getAddress());
        Date date = new Date();
        studentUserDTO.setGmtCreate(date);
        studentUserDTO.setGmtModified(date);
        apartmentThreadLocal.remove();
        return studentUserDTO;
    }

    @Override
    protected Tuple2<String, String> getUserNameAndPassword(StudentUserDTO studentUserDto) {
        // 使用学号作为用户名，姓名作为密码（初始密码）
        String username = studentUserDto.getStudentId();
        String password = studentUserDto.getName();
        return new Tuple2<>(username, password);
    }
}
