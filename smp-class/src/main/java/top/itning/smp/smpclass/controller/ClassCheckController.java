package top.itning.smp.smpclass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import top.itning.smp.smpclass.entity.RestModel;
import top.itning.smp.smpclass.security.LoginUser;
import top.itning.smp.smpclass.security.MustStudentLogin;
import top.itning.smp.smpclass.service.ClassCheckService;

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
}
