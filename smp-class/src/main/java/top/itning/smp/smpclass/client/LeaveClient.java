package top.itning.smp.smpclass.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.itning.smp.smpclass.client.entity.LeaveDTO;

import java.util.List;

/**
 * @author itning
 */
@FeignClient(name = "leave")
@Component
public interface LeaveClient {
    /**
     * 获取请假信息
     *
     * @param whereDay 哪天
     * @return 所有请假信息
     */
    @GetMapping("/internal/leaves")
    List<LeaveDTO> getAllLeave(@RequestParam("whereDay") String whereDay);
}
