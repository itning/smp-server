package top.itning.smp.smpclass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.itning.smp.smpclass.entity.RestModel;
import top.itning.smp.smpclass.security.LoginUser;
import top.itning.smp.smpclass.security.MustStudentLogin;
import top.itning.smp.smpclass.security.MustTeacherLogin;
import top.itning.smp.smpclass.service.ClassUserService;

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
}
