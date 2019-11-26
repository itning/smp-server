package top.itning.smp.smproom.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.itning.smp.smproom.client.entity.LeaveDTO;
import top.itning.smp.smproom.client.entity.LeaveType;

import java.util.List;

/**
 * @author itning
 */
@FeignClient(name = "leave")
@Component
public interface LeaveClient {
    /**
     * 获取正在生效的请假信息数量
     *
     * @param date 哪天开始
     * @return 正在生效的请假信息数量
     */
    @GetMapping("/internal/leaves/inEffect/count")
    long countInEffectLeaves(@RequestParam("date") String date);

    /**
     * 学生今天是否请假了
     *
     * @param userName  学生
     * @param leaveType 请假类型 只能传课假或寝室假，默认包括全部假
     * @return 今天请假了返回<code>true</code>
     */
    @GetMapping("/internal/isLeave")
    boolean isLeave(@RequestParam("userName") String userName, @RequestParam("leaveType") LeaveType leaveType);

    /**
     * 获取请假信息
     *
     * @param whereDay 哪天
     * @return 所有请假信息
     */
    @GetMapping("/internal/leaves")
    List<LeaveDTO> getAllLeave(@RequestParam("whereDay") String whereDay);
}
