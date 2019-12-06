package top.itning.smp.smpstatistics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author itning
 */
@FeignClient(name = "leave")
@Component
public interface LeaveClient {
    /**
     * 计算请假人数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param username  导员用户名，如果为空则不要这个查询条件
     * @return 请假人数
     */
    @GetMapping("/internal/leave/count")
    long countAllLeave(@RequestParam String startDate, @RequestParam String endDate, @RequestParam(required = false) String username);
}
