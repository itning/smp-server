package top.itning.smp.smpclass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.itning.smp.smpclass.entity.RestModel;
import top.itning.smp.smpclass.security.LoginUser;
import top.itning.smp.smpclass.security.MustStudentLogin;
import top.itning.smp.smpclass.security.MustTeacherLogin;
import top.itning.smp.smpclass.service.ClassCheckService;

import java.util.Date;

/**
 * @author itning
 */
@RestController
public class ClassCheckController {
    private final ClassCheckService studentClassCheckService;

    @Autowired
    public ClassCheckController(ClassCheckService studentClassCheckService) {
        this.studentClassCheckService = studentClassCheckService;
    }

    /**
     * 获取学生签到信息
     *
     * @param pageable       分页
     * @param studentClassId 学生课堂ID
     * @return 学生签到信息
     */
    @GetMapping("/checks/{studentClassId}")
    public ResponseEntity<?> getAllChecks(@PageableDefault(size = 20, sort = {"gmtCreate"}, direction = Sort.Direction.DESC)
                                                  Pageable pageable,
                                          @MustStudentLogin LoginUser loginUser,
                                          @PathVariable String studentClassId) {
        return RestModel.ok(studentClassCheckService.getAllChecks(studentClassId, loginUser, pageable));
    }

    /**
     * 检查是否可以签到
     *
     * @param studentClassId 学生班级ID
     * @return ResponseEntity
     */
    @GetMapping("/can_check/{studentClassId}")
    public ResponseEntity<?> canCheck(@MustStudentLogin LoginUser loginUser, @PathVariable String studentClassId) {
        return RestModel.ok(studentClassCheckService.canCheck(studentClassId, loginUser));
    }

    /**
     * 学生课堂签到
     *
     * @param longitude      经度
     * @param latitude       纬度
     * @param studentClassId 课堂ID
     * @return ResponseEntity
     */
    @PostMapping("/check")
    public ResponseEntity<?> check(@MustStudentLogin LoginUser loginUser,
                                   @RequestParam("longitude") double longitude,
                                   @RequestParam("latitude") double latitude,
                                   @RequestParam("studentClassId") String studentClassId) {
        return RestModel.created(studentClassCheckService.check(loginUser, studentClassId, longitude, latitude));
    }

    /**
     * 教师发起签到
     *
     * @param longitude      经度
     * @param latitude       纬度
     * @param studentClassId 课堂ID
     * @param m              最远签到距离（米）
     * @param startTime      签到开始时间
     * @param endTime        签到结束时间
     * @return ResponseEntity
     */
    @PostMapping("/new_check")
    public ResponseEntity<?> newCheck(@MustTeacherLogin LoginUser loginUser,
                                      @RequestParam("longitude") double longitude,
                                      @RequestParam("latitude") double latitude,
                                      @RequestParam("studentClassId") String studentClassId,
                                      @RequestParam("m") float m,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                      @RequestParam("startTime") Date startTime,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                      @RequestParam("endTime") Date endTime) {
        return RestModel.created(studentClassCheckService.newCheck(loginUser, longitude, latitude, studentClassId, m, startTime, endTime));
    }
}
