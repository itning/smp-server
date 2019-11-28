package top.itning.smp.smpclass.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.itning.smp.smpclass.entity.StudentUser;
import top.itning.smp.smpclass.entity.User;

import java.util.Optional;

/**
 * @author itning
 */
@FeignClient(name = "info")
@Component
public interface InfoClient {
    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户
     */
    @GetMapping("/internal/user/{username}")
    Optional<User> getUserInfoByUserName(@PathVariable String username);

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户
     */
    @GetMapping("/internal/student_user/{username}")
    Optional<StudentUser> getStudentUserInfoByUserName(@PathVariable String username);
}
