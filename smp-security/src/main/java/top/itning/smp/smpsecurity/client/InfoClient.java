package top.itning.smp.smpsecurity.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.itning.smp.smpsecurity.client.entity.StudentUserDTO;
import top.itning.smp.smpsecurity.client.entity.User;

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
     * 更改密码
     *
     * @param username 用户名
     * @param newPwd   新密码
     * @return 更改成功返回<code>true</code>
     */
    @PostMapping("/internal/pwd/user/change")
    boolean changeUserPwd(@RequestParam String username, @RequestParam String newPwd);

    /**
     * 获取学生信息根据学号
     *
     * @param studentId 学号
     * @return 用户
     */
    @GetMapping("/internal/student_user_id/{studentId}")
    StudentUserDTO getStudentUserDtoByStudentId(@PathVariable String studentId);
}
