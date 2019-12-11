package top.itning.smp.smpinfo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author itning
 */
@FeignClient(name = "class")
@Component
public interface ClassClient {
    /**
     * 删除某学生加入的班级信息
     *
     * @param counselorUsername 辅导员用户名
     * @param studentUserName   学生用户名
     */
    @PostMapping("/internal/delete")
    void delClassUserInfo(@RequestParam String counselorUsername, @RequestParam String studentUserName);
}
