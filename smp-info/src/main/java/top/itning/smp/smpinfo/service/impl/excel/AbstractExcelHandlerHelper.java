package top.itning.smp.smpinfo.service.impl.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.itning.smp.smpinfo.dao.StudentUserDao;
import top.itning.smp.smpinfo.dao.UserDao;
import top.itning.smp.smpinfo.dto.StudentUserDTO;
import top.itning.smp.smpinfo.dto.UpFileDTO;
import top.itning.smp.smpinfo.entity.StudentUser;
import top.itning.smp.smpinfo.entity.User;
import top.itning.smp.smpinfo.util.OrikaUtils;
import top.itning.utils.tuple.Tuple2;

import java.util.*;

/**
 * <p>继承并实现{@link AbstractReadExcel2Save}全部方法
 * <p>子类应该实现该抽象类的抽象方法
 *
 * @author itning
 * @date 2020/1/21 19:46
 */
public abstract class AbstractExcelHandlerHelper extends AbstractReadExcel2Save {
    private static final Logger logger = LoggerFactory.getLogger(AbstractExcelHandlerHelper.class);

    private final Set<String> idCardSet = new HashSet<>(16);
    private final Set<String> studentIdSet = new HashSet<>(16);
    private final StudentUserDao studentUserDao;

    public AbstractExcelHandlerHelper(UserDao userDao, StudentUserDao studentUserDao) {
        super(userDao);
        this.studentUserDao = studentUserDao;
    }

    @Override
    protected StudentUserDTO handleEachRow(Row row, Sheet sheet, int rowIndex, int lastRowNum) {
        ExcelData excelData = new ExcelData.Builder().setRow(row).build();
        if (needSkip(excelData)) {
            logger.warn("已跳过第" + (rowIndex + 1) + "行");
            return null;
        }
        if (!checkExcelData(excelData, rowIndex)) {
            return null;
        }
        Map<String, Object> analysisIdCardMap = analysisIdCard(excelData.getIdCard(), rowIndex);
        if (analysisIdCardMap == null) {
            return null;
        }
        if (!checkExcelDuplicateContent(excelData, rowIndex)) {
            return null;
        }
        return generateStudentUserDto(excelData, new IdCardData(analysisIdCardMap));
    }

    @Override
    protected UpFileDTO handleError(Set<String> errorInfoSet) {
        return new UpFileDTO(null, null, errorInfoSet);
    }

    @Override
    protected UpFileDTO saveStudentInfo(List<StudentUserDTO> studentUserDtoList) {
        studentUserDtoList.forEach(studentUserDTO -> {
            User user = OrikaUtils.a2b(studentUserDTO, User.class);
            Tuple2<String, String> userNameAndPassword = getUserNameAndPassword(studentUserDTO);
            user.setUsername(userNameAndPassword.getT1());
            user.setPassword(userNameAndPassword.getT2());
            User savedUser = userDao.save(user);
            StudentUser studentUser = OrikaUtils.a2b(studentUserDTO, StudentUser.class);
            studentUser.setId(savedUser.getId());
            studentUser.setBelongCounselorId(counselorUser.getId());
            studentUserDao.save(studentUser);
        });
        long count = studentUserDao.count();
        return new UpFileDTO((long) studentUserDtoList.size(), count, null);
    }

    protected static class IdCardData {
        private Date birthday;
        private boolean sex;
        private int age;

        public IdCardData(Map<String, Object> analysisIdCardMap) {
            this.birthday = (Date) analysisIdCardMap.get("birthday");
            this.age = (int) analysisIdCardMap.get("age");
            this.sex = (boolean) analysisIdCardMap.get("sex");
        }

        public Date getBirthday() {
            return birthday;
        }

        public boolean isSex() {
            return sex;
        }

        public int getAge() {
            return age;
        }
    }

    /**
     * 检查Excel本身内容是否有重复
     *
     * @param excelData 行数据
     * @param rowIndex  行索引
     * @return 没有重复返回<code>true</code>
     */
    private boolean checkExcelDuplicateContent(ExcelData excelData, int rowIndex) {
        boolean result = true;
        String studentId = excelData.getStudentId();
        String idCard = excelData.getIdCard();
        if (studentIdSet.contains(studentId)) {
            super.errorInfoSet.add(initErrorMsg(rowIndex, 4, "学号%s重复", studentId));
            result = false;
        } else {
            studentIdSet.add(studentId);
        }
        if (idCardSet.contains(idCard)) {
            super.errorInfoSet.add(initErrorMsg(rowIndex, 5, "身份证号%s重复", studentId));
            result = false;
        } else {
            idCardSet.add(idCard);
        }
        return result;
    }

    /**
     * 解析身份证号，如果解析错误则返回空
     *
     * @param idCard   身份证号
     * @param rowIndex 行号
     * @return 解析成功返回map，否则返回null
     */
    protected abstract Map<String, Object> analysisIdCard(String idCard, int rowIndex);

    /**
     * 检查Excel中数据是否正确，例如邮箱是否正确
     *
     * @param excelData 行信息
     * @param rowIndex  行号
     * @return 全部正确返回<code>true</code>
     */
    protected abstract boolean checkExcelData(ExcelData excelData, int rowIndex);

    /**
     * 生成学生信息用于保存
     *
     * @param excelData  行信息
     * @param idCardData 身份证信息实体
     * @return 学生信息
     */
    protected abstract StudentUserDTO generateStudentUserDto(ExcelData excelData, IdCardData idCardData);

    /**
     * 用户名密码生成规则
     *
     * @param studentUserDto 学生信息
     * @return 元组(用户名 ， 密码)
     */
    protected abstract Tuple2<String, String> getUserNameAndPassword(StudentUserDTO studentUserDto);

    /**
     * 初始化错误消息
     *
     * @param rowNum  行号
     * @param cellNum 列号
     * @param format  格式化
     * @param args    参数
     * @return 错误消息
     */
    protected String initErrorMsg(int rowNum, int cellNum, String format, Object... args) {
        String info = "在第" + (rowNum + 1) + "行" + "第" + cellNum + "列 ";
        return String.format(info + format, args);
    }

    private boolean needSkip(ExcelData excelData) {
        return StringUtils.isAllBlank(
                excelData.getName(),
                excelData.getTel(),
                excelData.getEmail(),
                excelData.getStudentId(),
                excelData.getIdCard(),
                excelData.getPoliticalStatus(),
                excelData.getEthnic(),
                excelData.getApartmentName(),
                excelData.getRoomNum(),
                excelData.getBedNum(),
                excelData.getAddress()
        );
    }
}
