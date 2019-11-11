package top.itning.smp.smpsecurity.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.itning.smp.smpsecurity.entity.RestModel;
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
}
