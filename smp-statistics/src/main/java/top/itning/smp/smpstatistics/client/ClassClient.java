package top.itning.smp.smpstatistics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.itning.smp.smpstatistics.entity.ClassComing;

/**
 * @author itning
 */
@FeignClient(name = "class")
@Component
public interface ClassClient {
    /**
     * 获取某天所有课堂出勤信息
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 出勤信息
     */
    @GetMapping("/internal/class_coming/count")
    ClassComing countLeave(@RequestParam String startDate, @RequestParam String endDate);
}
