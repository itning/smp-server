package top.itning.smp.smpclass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.itning.smp.smpclass.dto.ClassComingDTO;
import top.itning.smp.smpclass.entity.RestModel;
import top.itning.smp.smpclass.security.LoginUser;
import top.itning.smp.smpclass.security.MustLogin;
import top.itning.smp.smpclass.security.MustStudentLogin;
import top.itning.smp.smpclass.security.MustTeacherLogin;
import top.itning.smp.smpclass.service.ClassCheckService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static top.itning.smp.smpclass.security.MustLogin.ROLE.STUDENT;
import static top.itning.smp.smpclass.security.MustLogin.ROLE.TEACHER;

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
                                      @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
                                      @RequestParam("startTime") Date startTime,
                                      @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
                                      @RequestParam("endTime") Date endTime) {
        return RestModel.created(studentClassCheckService.newCheck(loginUser, longitude, latitude, studentClassId, m, startTime, endTime));
    }

    /**
     * 根据签到元数据获取该班级的签到信息
     *
     * @param studentClassCheckMetaDataId 元数据ID
     * @return ResponseEntity
     */
    @GetMapping("/check/{studentClassCheckMetaDataId}")
    public ResponseEntity<?> check(@MustTeacherLogin LoginUser loginUser,
                                   @PathVariable String studentClassCheckMetaDataId) {
        return RestModel.ok(studentClassCheckService.getCheckInfoByMetaDataId(studentClassCheckMetaDataId, loginUser));
    }

    /**
     * 获取签到信息
     *
     * @param studentUserName 学生用户名
     * @param studentClassId  班级ID
     * @return ResponseEntity
     */
    @GetMapping("/user_check_detail")
    public ResponseEntity<?> getUserCheckDetail(@MustLogin(role = {STUDENT, TEACHER}) LoginUser loginUser,
                                                @RequestParam String studentUserName,
                                                @RequestParam String studentClassId) {
        return RestModel.ok(studentClassCheckService.getUserCheckDetail(studentUserName, studentClassId, loginUser));
    }

    /**
     * 导出某班签到信息
     *
     * @param studentClassId 学生班级ID
     * @param response       HttpServletResponse
     * @throws IOException 导出异常
     */
    @GetMapping("/export/check")
    public void exportCheck(@MustTeacherLogin LoginUser loginUser,
                            @RequestParam String studentClassId,
                            HttpServletResponse response) throws IOException {
        studentClassCheckService.exportCheck(loginUser, studentClassId, response);
    }

    /**
     * 获取某天所有课堂出勤信息
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 出勤信息
     */
    @GetMapping("/internal/class_coming/count")
    public ClassComingDTO classComingCount(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                     @RequestParam Date startDate,
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                     @RequestParam Date endDate) {
        return studentClassCheckService.classComingCount(startDate, endDate);
    }
}
