package top.itning.smp.smpinfo.service.impl.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smpinfo.dao.UserDao;
import top.itning.smp.smpinfo.dto.StudentUserDTO;
import top.itning.smp.smpinfo.dto.UpFileDTO;
import top.itning.smp.smpinfo.entity.Role;
import top.itning.smp.smpinfo.entity.User;
import top.itning.smp.smpinfo.exception.FileException;
import top.itning.smp.smpinfo.security.LoginUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static top.itning.smp.smpinfo.entity.Role.STUDENT_ROLE_ID;

/**
 * <p>读取并保存Excel文件
 * <p>使用模板设置模式
 * <p>必须调用{@link #readAndSave(MultipartFile, LoginUser)}来初始化
 *
 * @author itning
 * @date 2020/1/21 19:57
 */
public abstract class AbstractReadExcel2Save {
    /**
     * XLS 文件扩展名
     */
    private static final String XLS_FILE = ".xls";
    /**
     * XLSX 文件扩展名
     */
    private static final String XLSX_FILE = ".xlsx";

    protected final UserDao userDao;

    protected Workbook workbook;
    protected Role studentRole;
    /**
     * Excel文件错误信息
     */
    protected Set<String> errorInfoSet = new TreeSet<>();
    /**
     * 辅导员用户
     */
    protected User counselorUser;

    public AbstractReadExcel2Save(UserDao userDao) {
        this.userDao = userDao;
    }

    public final UpFileDTO readAndSave(MultipartFile file, LoginUser loginUser) throws IOException {
        workbook = checkExcelFileCorrectnessAndGenerate(file);
        initCommonMetaData(loginUser);
        Sheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        List<StudentUserDTO> studentUserDtoList = new ArrayList<>();
        for (int i = 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            StudentUserDTO studentUserDto = handleEachRow(row, sheet, i, lastRowNum);
            if (studentUserDto != null) {
                studentUserDtoList.add(studentUserDto);
            }
        }
        if (errorInfoSet.isEmpty()) {
            return saveStudentInfo(studentUserDtoList);
        } else {
            return handleError(errorInfoSet);
        }
    }

    /**
     * 处理错误
     *
     * @param errorInfoSet 错误
     * @return UpFileDTO
     */
    protected abstract UpFileDTO handleError(Set<String> errorInfoSet);

    /**
     * 保存学生信息
     *
     * @param studentUserDtoList 学生集合
     * @return UpFileDTO
     */
    protected abstract UpFileDTO saveStudentInfo(List<StudentUserDTO> studentUserDtoList);

    /**
     * 处理每一行Excel信息
     *
     * @param row        当前行
     * @param rowIndex   当前行号
     * @param sheet      表格
     * @param lastRowNum 最后一行行号
     * @return 处理后生成的可用于持久化的学生信息
     */
    protected abstract StudentUserDTO handleEachRow(Row row, Sheet sheet, int rowIndex, int lastRowNum);

    /**
     * 初始化一些公共信息，例如辅导员用户信息和学生角色对象
     *
     * @param loginUser 登录用户（辅导员）
     */
    private void initCommonMetaData(LoginUser loginUser) {
        counselorUser = userDao.findByUsername(loginUser.getUsername());
        studentRole = new Role();
        studentRole.setId(STUDENT_ROLE_ID);
    }

    /**
     * 检查Excel文件正确性，如果没有问题生成{@link Workbook}对象实例
     *
     * @param file 文件
     */
    private Workbook checkExcelFileCorrectnessAndGenerate(MultipartFile file) throws IOException {
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
        return workbook;
    }
}
