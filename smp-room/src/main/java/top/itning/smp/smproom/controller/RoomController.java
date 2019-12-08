package top.itning.smp.smproom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smproom.entity.RestModel;
import top.itning.smp.smproom.security.LoginUser;
import top.itning.smp.smproom.security.MustCounselorLogin;
import top.itning.smp.smproom.security.MustStudentLogin;
import top.itning.smp.smproom.service.RoomCheckMetaDataService;
import top.itning.smp.smproom.service.RoomService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 寝室控制器
 *
 * @author itning
 */
@RestController
public class RoomController {
    private final RoomService roomService;
    private final RoomCheckMetaDataService roomCheckMetaDataService;

    @Autowired
    public RoomController(RoomService roomService, RoomCheckMetaDataService roomCheckMetaDataService) {
        this.roomService = roomService;
        this.roomCheckMetaDataService = roomCheckMetaDataService;
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        binder.registerCustomEditor(
                Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    /**
     * 获取学生的寝室打卡信息
     *
     * @param pageable 分页
     * @return ResponseEntity
     */
    @GetMapping("/checks")
    public ResponseEntity<?> getStudentCheckInfo(@PageableDefault(size = 20, sort = {"checkTime"}, direction = Sort.Direction.DESC)
                                                         Pageable pageable,
                                                 @MustStudentLogin LoginUser loginUser) {
        return RestModel.ok(roomService.getRoomCheckInfoByStudentUserName(loginUser.getUsername(), pageable));
    }

    /**
     * 检查是否允许打卡
     *
     * @return ResponseEntity
     */
    @GetMapping("/allow_check")
    public ResponseEntity<?> allowCheck(@MustStudentLogin LoginUser loginUser) {
        return RestModel.ok(roomCheckMetaDataService.isNowCanRoomCheck(loginUser));
    }

    /**
     * 获取学生寝室允许打卡时间
     *
     * @return ResponseEntity
     */
    @GetMapping("/check_date")
    public ResponseEntity<?> getCheckDate(@MustCounselorLogin LoginUser loginUser) {
        return RestModel.ok(roomCheckMetaDataService.getStudentCheckDate(loginUser));
    }

    /**
     * 修改学生寝室允许打卡时间
     *
     * @return ResponseEntity
     */
    @PostMapping("/check_date")
    public ResponseEntity<?> getCheckDate(@MustCounselorLogin LoginUser loginUser,
                                          String dateString) {
        return RestModel.created(roomCheckMetaDataService.upStudentCheckDate(dateString, loginUser));
    }

    /**
     * 获取允许打卡GPS坐标范围信息
     *
     * @return ResponseEntity
     */
    @GetMapping("/gps_range")
    public ResponseEntity<?> getGpsRangeInfo(@MustCounselorLogin LoginUser loginUser) {
        return RestModel.ok(roomCheckMetaDataService.getGpsRange(loginUser, false));
    }

    /**
     * 修改允许打卡GPS坐标范围信息
     *
     * @return ResponseEntity
     */
    @PostMapping("/gps_range")
    public ResponseEntity<?> upGpsRangeInfo(@MustCounselorLogin LoginUser loginUser, String gps) {
        return RestModel.created(roomCheckMetaDataService.upGpsRange(gps, loginUser));
    }

    /**
     * 学生打卡
     *
     * @param file      图片
     * @param longitude 经度
     * @param latitude  纬度
     * @return 打卡信息
     * @throws IOException 保存异常
     */
    @PostMapping("/check")
    public ResponseEntity<?> check(@MustStudentLogin LoginUser loginUser,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestParam("longitude") double longitude,
                                   @RequestParam("latitude") double latitude) throws IOException {
        return RestModel.created(roomService.check(file, loginUser, longitude, latitude));
    }

    /**
     * 获取学生某天打卡信息
     *
     * @param whereDay 哪天 默认今天
     * @return ResponseEntity
     */
    @GetMapping("/check_all")
    public ResponseEntity<?> checkAll(@MustCounselorLogin LoginUser loginUser,
                                      Date whereDay) {
        if (whereDay == null) {
            whereDay = new Date();
        }
        return RestModel.ok(roomService.checkAll(whereDay, loginUser));
    }

    /**
     * 计算应该打卡的学生数量（学生总数-请假生效中）
     *
     * @return 应打卡学生数量
     */
    @GetMapping("/countShouldRoomCheck")
    public ResponseEntity<?> countShouldRoomCheck(@MustCounselorLogin LoginUser loginUser,
                                                  @RequestParam("date") String date) {
        return RestModel.ok(roomService.countShouldRoomCheck(date, loginUser));
    }

    /**
     * 导出学生寝室打卡EXCEL
     *
     * @param whereDay 哪天
     * @param response HttpServletResponse
     * @throws IOException 可能出现的异常
     */
    @GetMapping("/export_room/{whereDay}")
    public void exportRoomInfo(@PathVariable("whereDay") Date whereDay,
                               @MustCounselorLogin LoginUser loginUser,
                               HttpServletResponse response) throws IOException {
        roomService.export(response.getOutputStream(), whereDay, loginUser);
    }

    /**
     * 计算某天归寝人数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param username  导员用户名
     * @return 人数
     */
    @GetMapping("/internal/coming/count/{username}")
    public long comingRoomCount(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam Date startDate,
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam Date endDate,
                                @PathVariable String username) {
        return roomService.comingRoomCount(username, startDate, endDate);
    }

    /**
     * 学生注册人脸
     *
     * @param file 文件
     * @return ResponseEntity
     * @throws IOException IOException
     */
    @PostMapping("/face/register")
    public ResponseEntity<?> registerFace(@MustStudentLogin LoginUser loginUser,
                                          @RequestParam("file") MultipartFile file) throws IOException {
        return RestModel.created(roomService.registerFace(file, loginUser));
    }
}
