package top.itning.smp.smpclass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.itning.smp.smpclass.entity.RestModel;
import top.itning.smp.smpclass.security.LoginUser;
import top.itning.smp.smpclass.security.MustStudentLogin;
import top.itning.smp.smpclass.service.ClassUserService;

/**
 * @author itning
 */
@RestController
public class ClassUserController {
    private final ClassUserService groupService;

    @Autowired
    public ClassUserController(ClassUserService groupService) {
        this.groupService = groupService;
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
        return RestModel.ok(groupService.getAllStudentClassUsers(loginUser, pageable));
    }

    /**
     * 加入班级
     *
     * @param classNum 班号
     * @return ResponseEntity
     */
    @PostMapping("/join_class")
    public ResponseEntity<?> joinClass(@MustStudentLogin LoginUser loginUser, @RequestParam String classNum) {
        return RestModel.created(groupService.joinClass(loginUser, classNum));
    }
}
