package top.itning.smp.smpstatistics.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.itning.smp.smpstatistics.client.ClassClient;
import top.itning.smp.smpstatistics.client.InfoClient;
import top.itning.smp.smpstatistics.client.LeaveClient;
import top.itning.smp.smpstatistics.client.RoomClient;
import top.itning.smp.smpstatistics.entity.*;
import top.itning.smp.smpstatistics.security.LoginUser;
import top.itning.smp.smpstatistics.service.StatisticsService;
import top.itning.smp.smpstatistics.util.DateUtils;
import top.itning.utils.tuple.Tuple2;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author itning
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final InfoClient infoClient;
    private final RoomClient roomClient;
    private final LeaveClient leaveClient;
    private final ClassClient classClient;

    @Autowired
    public StatisticsServiceImpl(InfoClient infoClient, RoomClient roomClient, LeaveClient leaveClient, ClassClient classClient) {
        this.infoClient = infoClient;
        this.roomClient = roomClient;
        this.leaveClient = leaveClient;
        this.classClient = classClient;
    }

    @Override
    public List<ApartmentStatistics> getApartmentChart() {
        return infoClient.getAllApartment();
    }

    @Override
    public HomeComing getHomeComingChart(Date date, LoginUser loginUser) {
        long countStudent = infoClient.countStudent(loginUser.getUsername());
        Tuple2<Date, Date> dateRange = DateUtils.getDateRange(date);
        long comingRoomCount = roomClient.comingRoomCount(
                LocalDateTime.ofInstant(dateRange.getT1().toInstant(), ZoneId.of("Asia/Shanghai")).toLocalDate().toString(),
                LocalDateTime.ofInstant(dateRange.getT2().toInstant(), ZoneId.of("Asia/Shanghai")).toLocalDate().toString(),
                loginUser.getUsername());
        HomeComing homeComing = new HomeComing();
        homeComing.setSum(countStudent);
        homeComing.setComing(comingRoomCount);
        return homeComing;
    }

    @Override
    public Leave getLeaveChart(Date date, LoginUser loginUser) {
        long countStudent = infoClient.countStudent(loginUser.getUsername());
        Tuple2<Date, Date> dateRange = DateUtils.getDateRange(date);
        long allLeave = leaveClient.countAllLeave(
                LocalDateTime.ofInstant(dateRange.getT1().toInstant(), ZoneId.of("Asia/Shanghai")).toLocalDate().toString(),
                LocalDateTime.ofInstant(dateRange.getT2().toInstant(), ZoneId.of("Asia/Shanghai")).toLocalDate().toString(),
                loginUser.getUsername());
        Leave leave = new Leave();
        leave.setSum(countStudent);
        leave.setLeave(allLeave);
        return leave;
    }

    @Override
    public ClassComing getClassComingChart(Date date, LoginUser loginUser) {
        Tuple2<Date, Date> dateRange = DateUtils.getDateRange(date);
        return classClient.classComingCount(
                LocalDateTime.ofInstant(dateRange.getT1().toInstant(), ZoneId.of("Asia/Shanghai")).toLocalDate().toString(),
                LocalDateTime.ofInstant(dateRange.getT2().toInstant(), ZoneId.of("Asia/Shanghai")).toLocalDate().toString()
        );
    }

    @Override
    public List<AllCounselor> getAllCounselorChart(LoginUser loginUser, Date startDate, Date endDate) {
        LocalDateTime localDateTime = DateUtils.date2LocalDateTime(endDate);
        LocalDateTime lastDateTime = DateUtils.date2LocalDateTime(startDate);
        String first = localDateTime.toLocalDate().toString();
        String last = lastDateTime.toLocalDate().toString();
        return infoClient.getAllCounselorUser()
                .parallelStream()
                .map(user -> {
                    ClassComing classComing = classClient.classComingCount(last, first);
                    AllCounselor allCounselor = new AllCounselor();
                    allCounselor.setClassComing(classComing);
                    allCounselor.setUser(user);

                    long countStudent = infoClient.countStudent(user.getUsername());
                    long allLeave = leaveClient.countAllLeave(last, first, user.getUsername());
                    Leave leave = new Leave();
                    leave.setSum(countStudent);
                    leave.setLeave(allLeave);
                    allCounselor.setLeave(leave);

                    long comingRoomCount = roomClient.comingRoomCount(last, first, user.getUsername());
                    HomeComing homeComing = new HomeComing();
                    homeComing.setSum(countStudent);
                    homeComing.setComing(comingRoomCount);
                    allCounselor.setHomeComing(homeComing);
                    return allCounselor;
                })
                .collect(Collectors.toList());
    }
}
