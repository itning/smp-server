package top.itning.smp.smproom.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author itning
 */
@FeignClient(name = "leave")
@Component
public interface LeaveClient {
    /**
     * 获取正在生效的请假信息数量
     *
     * @return 正在生效的请假信息数量
     */
    @GetMapping("/internal/leaves/inEffect/count")
    long countInEffectLeaves();
}
