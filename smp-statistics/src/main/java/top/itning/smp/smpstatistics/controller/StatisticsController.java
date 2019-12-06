package top.itning.smp.smpstatistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.itning.smp.smpstatistics.entity.RestModel;
import top.itning.smp.smpstatistics.security.LoginUser;
import top.itning.smp.smpstatistics.security.MustCounselorLogin;
import top.itning.smp.smpstatistics.service.StatisticsService;

import java.util.Date;

/**
 * @author itning
 */
@RestController
public class StatisticsController {
    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * 计算各公寓人数
     *
     * @return ResponseEntity
     */
    @GetMapping("/apartment")
    public ResponseEntity<?> getApartmentChart(@MustCounselorLogin LoginUser loginUser) {
        return RestModel.ok(statisticsService.getApartmentChart());
    }

    /**
     * 归寝信息
     *
     * @param date 哪天
     * @return 归寝信息
     */
    @GetMapping("/home_coming")
    public ResponseEntity<?> getHomeComingChart(@MustCounselorLogin LoginUser loginUser,
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                @RequestParam Date date) {
        return RestModel.ok(statisticsService.getHomeComingChart(date, loginUser));
    }

    /**
     * 请假信息
     *
     * @param date 哪天
     * @return 请假信息
     */
    @GetMapping("/leave")
    public ResponseEntity<?> getLeaveChart(@MustCounselorLogin LoginUser loginUser,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                           @RequestParam Date date) {
        return RestModel.ok(statisticsService.getLeaveChart(date, loginUser));
    }

    /**
     * 出勤信息
     *
     * @param date 哪天
     * @return 出勤信息
     */
    @GetMapping("/class_coming")
    public ResponseEntity<?> getClassComingChart(@MustCounselorLogin LoginUser loginUser,
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                 @RequestParam Date date) {
        return RestModel.ok(statisticsService.getClassComingChart(date, loginUser));
    }

    /**
     * 获取一个月内的辅导员各项数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return ResponseEntity
     */
    @GetMapping("/all_counselor")
    public ResponseEntity<?> getAllCounselorChart(@MustCounselorLogin LoginUser loginUser,
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                  @RequestParam Date startDate,
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                  @RequestParam Date endDate) {
        return RestModel.ok(statisticsService.getAllCounselorChart(loginUser, startDate, endDate));
    }
}
