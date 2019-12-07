package top.itning.smp.smpsecurity.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.itning.smp.smpsecurity.entity.RestModel;
import top.itning.smp.smpsecurity.security.LoginUser;
import top.itning.smp.smpsecurity.security.MustCounselorLogin;
import top.itning.smp.smpsecurity.security.MustLogin;
import top.itning.smp.smpsecurity.service.SecurityService;

/**
 * @author itning
 */
@RestController
public class SecurityController {
    private final SecurityService securityService;

    @Autowired
    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return ResponseEntity
     * @throws JsonProcessingException see {@link JsonProcessingException}
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("username") String username,
                                   @RequestParam("password") String password) throws JsonProcessingException {
        return RestModel.ok(securityService.login(username, password));
    }

    /**
     * 修改密码（登陆后操作）
     *
     * @param newPassword 新密码
     * @return ResponseEntity
     */
    @PostMapping("/change/password")
    public ResponseEntity<?> changePwd(@MustLogin(role = {MustLogin.ROLE.STUDENT, MustLogin.ROLE.TEACHER, MustLogin.ROLE.COUNSELOR}) LoginUser loginUser,
                                       @RequestParam("newPassword") String newPassword) {
        securityService.changePwd(loginUser, newPassword);
        return RestModel.noContent();
    }

    /**
     * 辅导员根据学号重置密码
     *
     * @param studentId 学号
     * @return ResponseEntity
     */
    @PostMapping("/reset/password")
    public ResponseEntity<?> resetPwd(@MustCounselorLogin LoginUser loginUser,
                                      @RequestParam("studentId") String studentId) {
        securityService.resetPwd(loginUser, studentId);
        return RestModel.noContent();
    }

    /**
     * 检查服务器连接
     *
     * @return pong
     */
    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return RestModel.ok("PONG");
    }
}
