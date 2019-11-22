package top.itning.smp.smproom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.itning.smp.smproom.entity.RestModel;
import top.itning.smp.smproom.security.LoginUser;
import top.itning.smp.smproom.security.MustStudentLogin;
import top.itning.smp.smproom.service.RoomService;

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
        System.out.println(pageable.getPageNumber());
        System.out.println(pageable.getPageSize());
        return RestModel.ok(roomService.getRoomCheckInfoByStudentUserName(loginUser.getUsername(), pageable));
    }
}
