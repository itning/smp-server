package top.itning.smp.smproom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smproom.entity.RestModel;
import top.itning.smp.smproom.security.LoginUser;
import top.itning.smp.smproom.security.MustStudentLogin;
import top.itning.smp.smproom.service.RoomService;

import java.io.IOException;

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
     * @param file 图片
     * @return 打卡信息
     * @throws IOException 保存异常
     */
    @PostMapping("/check")
    public ResponseEntity<?> check(@MustStudentLogin LoginUser loginUser,
                                   @RequestParam("file") MultipartFile file) throws IOException {
        return RestModel.created(roomService.check(file, loginUser));
    }
}
