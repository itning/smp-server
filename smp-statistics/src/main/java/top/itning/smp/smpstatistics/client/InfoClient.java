package top.itning.smp.smpstatistics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.itning.smp.smpstatistics.client.entity.User;
import top.itning.smp.smpstatistics.entity.ApartmentStatistics;

import java.util.List;
import java.util.Optional;

/**
 * @author itning
 */
@FeignClient(name = "info")
@Component
public interface InfoClient {
    /**
     * 获取所有公寓信息
     *
     * @return 公寓信息集合
     */
    @GetMapping("/internal/apartments")
    List<ApartmentStatistics> getAllApartment();

    /**
     * 计算学生人数
     *
     * @param username 导员用户名
     * @return 学生数量
     */
    @GetMapping("/internal/student_user/count/{username}")
    long countStudent(@PathVariable String username);

    /**
     * 获取所有辅导员用户
     *
     * @return 辅导员用户
     */
    @GetMapping("/internal/counselor/users")
    List<User> getAllCounselorUser();

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户
     */
    @GetMapping("/internal/user/{username}")
    Optional<User> getUserInfoByUserName(@PathVariable String username);
}
