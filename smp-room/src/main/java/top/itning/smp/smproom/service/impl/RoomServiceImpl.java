package top.itning.smp.smproom.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smproom.client.InfoClient;
import top.itning.smp.smproom.client.LeaveClient;
import top.itning.smp.smproom.client.entity.LeaveDTO;
import top.itning.smp.smproom.client.entity.LeaveType;
import top.itning.smp.smproom.client.entity.StudentUserDTO;
import top.itning.smp.smproom.config.CustomProperties;
import top.itning.smp.smproom.dao.StudentRoomCheckDao;
import top.itning.smp.smproom.entity.StudentRoomCheck;
import top.itning.smp.smproom.entity.StudentUser;
import top.itning.smp.smproom.entity.User;
import top.itning.smp.smproom.exception.GpsException;
import top.itning.smp.smproom.exception.IllegalCheckException;
import top.itning.smp.smproom.exception.SavedException;
import top.itning.smp.smproom.exception.UserNameDoesNotExistException;
import top.itning.smp.smproom.security.LoginUser;
import top.itning.smp.smproom.service.RoomCheckMetaDataService;
import top.itning.smp.smproom.service.RoomService;
import top.itning.smp.smproom.util.DateUtils;
import top.itning.smp.smproom.util.GpsUtils;
import top.itning.utils.tuple.Tuple2;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static top.itning.smp.smproom.util.DateUtils.getDateRange;
import static top.itning.smp.smproom.util.GpsUtils.*;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoomServiceImpl implements RoomService {
    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);
    private final StudentRoomCheckDao studentRoomCheckDao;
    private final InfoClient infoClient;
    private final LeaveClient leaveClient;
    private final CustomProperties customProperties;
    private final RoomCheckMetaDataService roomCheckMetaDataService;

    @Autowired
    public RoomServiceImpl(StudentRoomCheckDao studentRoomCheckDao, InfoClient infoClient, LeaveClient leaveClient, CustomProperties customProperties, RoomCheckMetaDataService roomCheckMetaDataService) {
        this.studentRoomCheckDao = studentRoomCheckDao;
        this.infoClient = infoClient;
        this.leaveClient = leaveClient;
        this.customProperties = customProperties;
        this.roomCheckMetaDataService = roomCheckMetaDataService;
    }

    @Override
    public Page<StudentRoomCheck> getRoomCheckInfoByStudentUserName(String username, Pageable pageable) {
        User user = infoClient.getUserInfoByUserName(username).orElseThrow(() -> new UserNameDoesNotExistException("用户名不存在", HttpStatus.NOT_FOUND));
        return studentRoomCheckDao.findAllByUser(user, pageable);
    }

    @Override
    public StudentRoomCheck check(MultipartFile file, LoginUser loginUser, double longitude, double latitude) throws IOException {
        // 修正坐标
        if (longitude > MAX_LONGITUDE || longitude < MIN_LONGITUDE || latitude > MAX_LATITUDE || latitude < MIN_LATITUDE) {
            throw new GpsException(longitude, latitude);
        }
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> new UserNameDoesNotExistException("用户名不存在", HttpStatus.NOT_FOUND));
        if (leaveClient.isLeave(user.getUsername(), LeaveType.ROOM_LEAVE)) {
            throw new IllegalCheckException("您今天已经请假了，无需打卡");
        }
        Tuple2<Date, Date> dateRange = getDateRange(new Date());
        if (studentRoomCheckDao.existsByUserAndCheckTimeBetween(user, dateRange.getT1(), dateRange.getT2())) {
            throw new IllegalCheckException("您今天已经打过卡了，不能重复打卡");
        }
        if (!GpsUtils.isPtInPoly(longitude, latitude, roomCheckMetaDataService.getGpsRange(loginUser, true))) {
            throw new IllegalCheckException("打卡所在位置不在辅导员指定的区域内");
        }
        StudentRoomCheck studentRoomCheck = new StudentRoomCheck();
        studentRoomCheck.setUser(user);
        studentRoomCheck.setLongitude(longitude);
        studentRoomCheck.setLatitude(latitude);
        studentRoomCheck.setCheckTime(new Date());
        StudentRoomCheck saved = studentRoomCheckDao.save(studentRoomCheck);
        String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (filenameExtension == null) {
            logger.warn("use default extension for path {}", file.getOriginalFilename());
            filenameExtension = "jpg";
        }
        if (!StringUtils.hasText(saved.getId())) {
            throw new SavedException("数据库存储ID为空", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        file.transferTo(new File(customProperties.getResourceLocation() + saved.getId() + "." + filenameExtension));
        return saved;
    }

    @Override
    public List<StudentRoomCheck> checkAll(Date whereDay, LoginUser loginUser) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> new UserNameDoesNotExistException("用户名不存在", HttpStatus.NOT_FOUND));
        return studentRoomCheckDao.findAll((Specification<StudentRoomCheck>) (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();

            Tuple2<Date, Date> dateRange = getDateRange(whereDay);
            list.add(cb.between(root.get("checkTime"), dateRange.getT1(), dateRange.getT2()));

            Join<StudentRoomCheck, User> userJoin = root.join("user", JoinType.INNER);
            Join<User, StudentUser> studentUserJoin = userJoin.join("studentUser", JoinType.INNER);
            list.add(cb.equal(studentUserJoin.get("belongCounselorId"), user.getId()));

            query.orderBy(cb.desc(root.get("checkTime")));

            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        });
    }

    @Override
    public Tuple2<Long, Long> countShouldRoomCheck(String date, LoginUser loginUser) {
        long countStudent = infoClient.countStudent(loginUser.getUsername());
        long countInEffectLeaves = leaveClient.countInEffectLeaves(date, loginUser.getUsername());
        logger.debug("countStudent {} countInEffectLeaves {}", countStudent, countInEffectLeaves);
        return new Tuple2<>(countStudent, countInEffectLeaves);
    }

    @Override
    public void export(OutputStream outputStream, Date whereDay, LoginUser loginUser) throws IOException {
        XSSFWorkbook sheets = new XSSFWorkbook();
        XSSFSheet sheet = sheets.createSheet();
        XSSFRow headerRow = sheet.createRow(0);
        CreationHelper helper = sheets.getCreationHelper();
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        initExportHeader(headerRow);
        // 所有学生
        List<StudentUserDTO> allUser = infoClient.getAllUser(loginUser.getUsername());
        // 所有打卡同学
        List<StudentRoomCheck> studentRoomCheckList = this.checkAll(whereDay, loginUser);
        // 所有请假同学
        List<LeaveDTO> allLeave = leaveClient.getAllLeave(DateUtils.format(whereDay, DateUtils.YYYYMMDD_DATE_TIME_FORMATTER_1), loginUser.getUsername());
        // 所有未打卡同学（排除请假的同学）
        List<StudentUserDTO> unCheckList = allUser
                .parallelStream()
                .filter(studentUserDTO -> {
                    if (studentRoomCheckList.isEmpty()) {
                        return true;
                    }
                    return studentRoomCheckList.parallelStream().noneMatch(studentRoomCheck -> studentRoomCheck.getUser().getId().equals(studentUserDTO.getId()));
                })
                .filter(studentUserDTO -> {
                    if (allLeave.isEmpty()) {
                        return true;
                    }
                    return allLeave.parallelStream().noneMatch(leaveDTO -> leaveDTO.getStudentUser().getId().equals(studentUserDTO.getId()));
                })
                .collect(Collectors.toList());
        // 背景红色
        XSSFCellStyle redBackColorCellStyle = sheets.createCellStyle();
        redBackColorCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        redBackColorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 背景黄色
        XSSFCellStyle yellowBackColorCellStyle = sheets.createCellStyle();
        yellowBackColorCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yellowBackColorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 最后一行变量
        int lastRowNum = studentRoomCheckList.size() + 1;
        // 添加打卡同学信息
        addRoomCheckInfo(sheets, helper, drawing, sheet, studentRoomCheckList);
        // 添加请假同学信息
        addLeaveInfo(sheet, allUser, allLeave, yellowBackColorCellStyle, lastRowNum);
        // 更新最后一行变量
        lastRowNum += allLeave.size();
        // 添加未打卡同学信息
        addUnCheckInfo(sheet, unCheckList, redBackColorCellStyle, lastRowNum);
        sheets.write(outputStream);
    }

    /**
     * 添加未打卡同学信息
     *
     * @param sheet                 工作表
     * @param unCheckList           未打卡同学信息集合
     * @param redBackColorCellStyle 背景颜色
     * @param lastRowNum            行号
     */
    private void addUnCheckInfo(XSSFSheet sheet, List<StudentUserDTO> unCheckList, XSSFCellStyle redBackColorCellStyle, int lastRowNum) {
        for (int i = 0; i < unCheckList.size(); i++) {
            XSSFRow row = sheet.createRow(lastRowNum + i);
            StudentUserDTO studentUserDTO = unCheckList.get(i);

            XSSFCell c0 = row.createCell(0);
            c0.setCellStyle(redBackColorCellStyle);
            c0.setCellValue(studentUserDTO.getName());

            XSSFCell c1 = row.createCell(1);
            c1.setCellStyle(redBackColorCellStyle);
            c1.setCellValue(studentUserDTO.getStudentId());

            XSSFCell c2 = row.createCell(2);
            c2.setCellStyle(redBackColorCellStyle);
            c2.setCellValue(studentUserDTO.getApartment().getName());

            XSSFCell c3 = row.createCell(3);
            c3.setCellStyle(redBackColorCellStyle);
            c3.setCellValue(studentUserDTO.getRoomNum());

            XSSFCell c4 = row.createCell(4);
            c4.setCellStyle(redBackColorCellStyle);
            c4.setCellValue(studentUserDTO.getBedNum());

            XSSFCell c5 = row.createCell(5);
            c5.setCellStyle(redBackColorCellStyle);
            c5.setCellValue(studentUserDTO.getTel());
        }
    }

    /**
     * 添加请假学生信息
     *
     * @param sheet                    工作表
     * @param allUser                  所有学生集合
     * @param allLeave                 请假学生集合
     * @param yellowBackColorCellStyle 背景颜色
     * @param lastRowNum               行号
     */
    private void addLeaveInfo(XSSFSheet sheet, List<StudentUserDTO> allUser, List<LeaveDTO> allLeave, XSSFCellStyle yellowBackColorCellStyle, int lastRowNum) {
        for (int i = 0; i < allLeave.size(); i++) {
            XSSFRow row = sheet.createRow(lastRowNum + i);
            LeaveDTO leaveDTO = allLeave.get(i);
            XSSFCell c0 = row.createCell(0);

            c0.setCellStyle(yellowBackColorCellStyle);
            Optional<StudentUserDTO> studentUserDtoOptional = allUser.stream().filter(studentUserDTO -> studentUserDTO.getId().equals(leaveDTO.getStudentUser().getId())).findFirst();
            StudentUserDTO studentUserDTO = studentUserDtoOptional.orElse(new StudentUserDTO());
            c0.setCellValue(studentUserDTO.getName());

            XSSFCell c1 = row.createCell(1);
            c1.setCellStyle(yellowBackColorCellStyle);
            c1.setCellValue(leaveDTO.getStudentUser().getStudentId());

            XSSFCell c2 = row.createCell(2);
            c2.setCellStyle(yellowBackColorCellStyle);
            c2.setCellValue(leaveDTO.getStudentUser().getApartment().getName());

            XSSFCell c3 = row.createCell(3);
            c3.setCellStyle(yellowBackColorCellStyle);
            c3.setCellValue(leaveDTO.getStudentUser().getRoomNum());

            XSSFCell c4 = row.createCell(4);
            c4.setCellStyle(yellowBackColorCellStyle);
            c4.setCellValue(leaveDTO.getStudentUser().getBedNum());

            XSSFCell c5 = row.createCell(5);
            c5.setCellStyle(yellowBackColorCellStyle);
            c5.setCellValue(studentUserDTO.getTel());
        }
    }

    /**
     * 添加打卡的学生信息
     *
     * @param workbook             工作簿
     * @param helper               CreationHelper
     * @param drawing              Drawing
     * @param sheet                工作表
     * @param studentRoomCheckList 打卡的学生信息集合
     */
    private void addRoomCheckInfo(XSSFWorkbook workbook, CreationHelper helper, XSSFDrawing drawing, XSSFSheet sheet, List<StudentRoomCheck> studentRoomCheckList) {
        for (int i = 1; i <= studentRoomCheckList.size(); i++) {
            XSSFRow row = sheet.createRow(i);
            StudentRoomCheck studentRoomCheck = studentRoomCheckList.get(i - 1);
            row.createCell(0).setCellValue(studentRoomCheck.getUser().getName());
            row.createCell(1).setCellValue(studentRoomCheck.getUser().getStudentUser().getStudentId());
            row.createCell(2).setCellValue(studentRoomCheck.getUser().getStudentUser().getApartment().getName());
            row.createCell(3).setCellValue(studentRoomCheck.getUser().getStudentUser().getRoomNum());
            row.createCell(4).setCellValue(studentRoomCheck.getUser().getStudentUser().getBedNum());
            row.createCell(5).setCellValue(studentRoomCheck.getUser().getTel());
            row.createCell(6).setCellValue(DateUtils.format(studentRoomCheck.getCheckTime(), DateUtils.YYYYMMDDHHMMSS_DATE_TIME_FORMATTER_2));
            row.createCell(7).setCellValue(studentRoomCheck.getLongitude() + "," + studentRoomCheck.getLatitude());
            addPic(workbook, helper, drawing, 8, i, customProperties.getResourceLocation() + studentRoomCheck.getId() + ".jpg");
        }
    }

    /**
     * 添加图片
     *
     * @param workbook 工作簿
     * @param helper   CreationHelper
     * @param drawing  Drawing
     * @param col      要插入的列
     * @param row      要插入的行
     * @param filePath 文件路径
     */
    @SuppressWarnings("SameParameterValue")
    private void addPic(XSSFWorkbook workbook, CreationHelper helper, XSSFDrawing drawing, int col, int row, String filePath) {
        try (FileInputStream is = new FileInputStream(filePath)) {
            int pictureIdx = workbook.addPicture(is, Workbook.PICTURE_TYPE_JPEG);
            ClientAnchor anchor = helper.createClientAnchor();
            // 图片插入坐标
            anchor.setCol1(col);
            anchor.setRow1(row);
            // 插入图片
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            pict.resize(1, 1);
        } catch (Exception e) {
            logger.warn("Add pic exception and file path: " + filePath, e);
        }
    }

    /**
     * 初始化表头
     *
     * @param headerRow 表头所在行
     */
    private void initExportHeader(XSSFRow headerRow) {
        XSSFCell c0 = headerRow.createCell(0);
        c0.setCellValue("姓名");
        XSSFCell c1 = headerRow.createCell(1);
        c1.setCellValue("学号");
        XSSFCell c2 = headerRow.createCell(2);
        c2.setCellValue("公寓");
        XSSFCell c3 = headerRow.createCell(3);
        c3.setCellValue("寝室");
        XSSFCell c4 = headerRow.createCell(4);
        c4.setCellValue("床铺");
        XSSFCell c5 = headerRow.createCell(5);
        c5.setCellValue("电话");
        XSSFCell c6 = headerRow.createCell(6);
        c6.setCellValue("打卡日期");
        XSSFCell c7 = headerRow.createCell(7);
        c7.setCellValue("坐标");
        XSSFCell c8 = headerRow.createCell(8);
        c8.setCellValue("照片");
    }
}
