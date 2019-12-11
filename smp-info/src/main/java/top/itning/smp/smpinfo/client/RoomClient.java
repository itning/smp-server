package top.itning.smp.smpinfo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author itning
 */
@FeignClient(name = "room")
@Component
public interface RoomClient {
    /**
     * 删除寝室打卡信息
     *
     * @param counselorUsername 导员用户名
     * @param studentUserName   学生用户名
     */
    @PostMapping("/internal/delete")
    void delRoomInfo(@RequestParam String counselorUsername, @RequestParam String studentUserName);
}
