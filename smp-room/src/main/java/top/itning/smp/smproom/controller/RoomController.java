package top.itning.smp.smproom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smproom.entity.RestModel;
import top.itning.smp.smproom.security.LoginUser;
import top.itning.smp.smproom.security.MustCounselorLogin;
import top.itning.smp.smproom.security.MustStudentLogin;
import top.itning.smp.smproom.service.RoomService;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
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
        return RestModel.ok(true);
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

    @GetMapping("/check_all")
    public ResponseEntity<?> checkAll(@MustCounselorLogin LoginUser loginUser,
                                      Date whereDay) {
        if (whereDay == null) {
            whereDay = new Date();
        }
        return RestModel.ok(roomService.checkAll(whereDay));
    }
}
