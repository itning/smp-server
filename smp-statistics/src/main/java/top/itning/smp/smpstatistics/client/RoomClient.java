package top.itning.smp.smpstatistics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author itning
 */
@FeignClient(name = "room")
@Component
public interface RoomClient {
    /**
     * 计算某天归寝人数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param username 导员用户名
     * @return 人数
     */
    @GetMapping("/internal/coming/count/{username}")
    long comingRoomCount(@RequestParam String startDate, @RequestParam String endDate, @PathVariable String username);
}
