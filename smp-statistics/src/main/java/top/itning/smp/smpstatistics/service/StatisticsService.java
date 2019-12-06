package top.itning.smp.smpstatistics.service;

import top.itning.smp.smpstatistics.entity.*;
import top.itning.smp.smpstatistics.security.LoginUser;

import java.util.Date;
import java.util.List;

/**
 * @author itning
 */
public interface StatisticsService {
    /**
     * 获取各公寓人数
     *
     * @return 各公寓人数集合
     */
    List<ApartmentStatistics> getApartmentChart();

    /**
     * 某天归寝信息
     *
     * @param date      哪天
     * @param loginUser 登录用户
     * @return 归寝信息
     */
    HomeComing getHomeComingChart(Date date, LoginUser loginUser);

    /**
     * 请假信息
     *
     * @param date      哪天
     * @param loginUser 登录用户
     * @return 请假信息
     */
    Leave getLeaveChart(Date date, LoginUser loginUser);

    /**
     * 出勤信息
     *
     * @param date      哪天
     * @param loginUser 登录用户
     * @return 出勤信息
     */
    ClassComing getClassComingChart(Date date, LoginUser loginUser);

    /**
     * 辅导员各项数据
     *
     * @param loginUser 登录用汉语
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 所有数据
     */
    List<AllCounselor> getAllCounselorChart(LoginUser loginUser, Date startDate, Date endDate);
}
