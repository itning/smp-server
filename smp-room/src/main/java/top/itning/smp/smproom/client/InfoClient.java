package top.itning.smp.smproom.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import top.itning.smp.smproom.client.entity.StudentUserDTO;
import top.itning.smp.smproom.entity.StudentUser;
import top.itning.smp.smproom.entity.User;

import java.util.List;
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

    /**
     * 计算学生人数
     *
     * @param username 导员用户名
     * @return 学生数量
     */
    @GetMapping("/internal/student_user/count/{username}")
    long countStudent(@PathVariable String username);

    /**
     * 获取所有学生信息
     *
     * @param username 导员用户名
     * @return 学生信息
     */
    @GetMapping("/internal/users")
    List<StudentUserDTO> getAllUser(@RequestParam String username);
}
