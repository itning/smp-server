package top.itning.smp.smpclass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smpclass.entity.RestModel;
import top.itning.smp.smpclass.security.LoginUser;
import top.itning.smp.smpclass.security.MustStudentLogin;
import top.itning.smp.smpclass.security.MustTeacherLogin;
import top.itning.smp.smpclass.service.ClassUserService;

import java.io.IOException;
import java.util.Date;

/**
 * @author itning
 */
@RestController
public class ClassUserController {
    private final ClassUserService classUserService;

    @Autowired
    public ClassUserController(ClassUserService classUserService) {
        this.classUserService = classUserService;
    }

    /**
     * 获取所有学生班级
     *
     * @param pageable 分页
     * @return 学生班级
     */
    @GetMapping("/student_class_users")
    public ResponseEntity<?> getAllStudentClassUser(@PageableDefault(size = 20, sort = {"gmtCreate"}, direction = Sort.Direction.DESC)
                                                            Pageable pageable,
                                                    @MustStudentLogin LoginUser loginUser) {
        return RestModel.ok(classUserService.getAllStudentClassUsers(loginUser, pageable));
    }

    /**
     * 加入班级
     *
     * @param classNum 班号
     * @return ResponseEntity
     */
    @PostMapping("/join_class")
    public ResponseEntity<?> joinClass(@MustStudentLogin LoginUser loginUser, @RequestParam String classNum) {
        return RestModel.created(classUserService.joinClass(loginUser, classNum));
    }

    /**
     * 学生退出班级
     *
     * @param studentClassId 班级ID
     * @return ResponseEntity
     */
    @PostMapping("/quit_class")
    public ResponseEntity<?> quitClass(@MustStudentLogin LoginUser loginUser, @RequestParam String studentClassId) {
        classUserService.quitClass(loginUser, studentClassId);
        return RestModel.noContent();
    }

    /**
     * 创建班级
     *
     * @param className 班级名
     * @return 创建的班级
     */
    @PostMapping("/new_class")
    public ResponseEntity<?> newClass(@MustTeacherLogin LoginUser loginUser, @RequestParam String className) {
        return RestModel.created(classUserService.newClass(loginUser, className));
    }

    /**
     * 删除班级
     *
     * @param studentClassId 班级ID
     * @return ResponseEntity
     */
    @PostMapping("/del_class")
    public ResponseEntity<?> delClass(@MustTeacherLogin LoginUser loginUser, @RequestParam String studentClassId) {
        classUserService.delClass(loginUser, studentClassId);
        return RestModel.noContent();
    }

    /**
     * 获取教师创建的班级
     *
     * @param pageable 分页
     * @return ResponseEntity
     */
    @GetMapping("/student_class")
    public ResponseEntity<?> studentClass(@PageableDefault(size = 20, sort = {"gmtCreate"}, direction = Sort.Direction.DESC)
                                                  Pageable pageable,
                                          @MustTeacherLogin LoginUser loginUser) {
        return RestModel.ok(classUserService.getAllStudentClass(loginUser, pageable));
    }

    /**
     * 获取所有签到元数据
     *
     * @param studentClassId 班级ID
     * @param pageable       分页
     * @return ResponseEntity
     */
    @GetMapping("/student_class_check_meta_data/{studentClassId}")
    public ResponseEntity<?> getAllStudentClassCheckMetaData(@PageableDefault(size = 20, sort = {"gmtCreate"}, direction = Sort.Direction.DESC)
                                                                     Pageable pageable,
                                                             @MustTeacherLogin LoginUser loginUser,
                                                             @PathVariable("studentClassId") String studentClassId) {
        return RestModel.ok(classUserService.getAllStudentClassCheckMetaData(studentClassId, loginUser, pageable));
    }

    /**
     * 获取班级请假信息
     *
     * @param studentClassId 班级ID
     * @param whereDay       哪天
     * @return ResponseEntity
     */
    @GetMapping("/student_class_leave")
    public ResponseEntity<?> getStudentClassLeave(@MustTeacherLogin LoginUser loginUser,
                                                  @RequestParam String studentClassId,
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                  @RequestParam("whereDay") Date whereDay) {
        return RestModel.ok(classUserService.getStudentClassLeave(loginUser, studentClassId, whereDay));
    }

    /**
     * 教师删除自己班级的学生
     *
     * @param studentUserName 用户名
     * @param studentClassId  班级ID
     * @return ResponseEntity
     */
    @PostMapping("/del_student")
    public ResponseEntity<?> delStudent(@MustTeacherLogin LoginUser loginUser,
                                        @RequestParam String studentUserName,
                                        @RequestParam String studentClassId) {
        classUserService.delStudent(studentUserName, studentClassId, loginUser);
        return RestModel.noContent();
    }

    /**
     * 通过EXCEL文件导入学生到某个班级
     * 每行格式<code>学号:2016022022</code>
     *
     * @param file           文件
     * @param studentClassId 班级ID
     * @return ResponseEntity
     * @throws IOException 文件解析异常
     */
    @PostMapping("/student_class/file/{studentClassId}")
    public ResponseEntity<?> upFile(@MustTeacherLogin LoginUser loginUser,
                                    @RequestParam("file") MultipartFile file,
                                    @PathVariable String studentClassId) throws IOException {
        return RestModel.created(classUserService.importStudentByFile(file, studentClassId, loginUser));
    }

    /**
     * 教师修改班级名称
     *
     * @param studentClassId      班级ID
     * @param newStudentClassName 新班级名称
     * @return ResponseEntity
     */
    @PostMapping("/student_class_name")
    public ResponseEntity<?> modifyStudentClassName(@MustTeacherLogin LoginUser loginUser,
                                                    @RequestParam String studentClassId,
                                                    @RequestParam String newStudentClassName) {
        classUserService.modifyStudentClassName(studentClassId, newStudentClassName, loginUser);
        return RestModel.noContent();
    }

    /**
     * 删除某学生加入的班级信息
     *
     * @param counselorUsername 辅导员用户名
     * @param studentUserName   学生用户名
     */
    @PostMapping("/internal/delete")
    public void delClassUserInfo(@RequestParam String counselorUsername,
                                 @RequestParam String studentUserName) {
        classUserService.delClassUserInfo(counselorUsername, studentUserName);
    }
}
