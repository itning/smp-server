package top.itning.smp.smproom.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smproom.client.InfoClient;
import top.itning.smp.smproom.dao.StudentRoomCheckMetaDataDao;
import top.itning.smp.smproom.entity.StudentRoomCheckMetaData;
import top.itning.smp.smproom.entity.StudentUser;
import top.itning.smp.smproom.entity.User;
import top.itning.smp.smproom.exception.AppMetaException;
import top.itning.smp.smproom.exception.UserNameDoesNotExistException;
import top.itning.smp.smproom.security.LoginUser;
import top.itning.smp.smproom.service.RoomCheckMetaDataService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static top.itning.smp.smproom.util.DateUtils.localDateTime2Date;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoomCheckMetaDataServiceImpl implements RoomCheckMetaDataService {
    /**
     * 时间分割完数组大小
     */
    private static final int TIME_SEPARATOR_ARRAY_LENGTH = 2;
    /**
     * 时间分割符
     */
    private static final String TIME_SEPARATOR = ":";

    private final StudentRoomCheckMetaDataDao studentRoomCheckMetaDataDao;
    private final InfoClient infoClient;

    @Autowired
    public RoomCheckMetaDataServiceImpl(StudentRoomCheckMetaDataDao studentRoomCheckMetaDataDao, InfoClient infoClient) {
        this.studentRoomCheckMetaDataDao = studentRoomCheckMetaDataDao;
        this.infoClient = infoClient;
    }

    private void initAllKey(String counselorId) {
        if (!studentRoomCheckMetaDataDao.existsByKeyAndBelongCounselorId(StudentRoomCheckMetaData.KEY_ROOM_CHECK_TIME, counselorId)) {
            StudentRoomCheckMetaData studentRoomCheckMetaData1 = new StudentRoomCheckMetaData();
            studentRoomCheckMetaData1.setKey(StudentRoomCheckMetaData.KEY_ROOM_CHECK_TIME);
            studentRoomCheckMetaData1.setValue("20:30");
            studentRoomCheckMetaData1.setBelongCounselorId(counselorId);
            studentRoomCheckMetaDataDao.save(studentRoomCheckMetaData1);
        }
        if (!studentRoomCheckMetaDataDao.existsByKeyAndBelongCounselorId(StudentRoomCheckMetaData.KEY_ROOM_CHECK_GPS_RANGE, counselorId)) {
            StudentRoomCheckMetaData studentRoomCheckMetaData2 = new StudentRoomCheckMetaData();
            studentRoomCheckMetaData2.setKey(StudentRoomCheckMetaData.KEY_ROOM_CHECK_GPS_RANGE);
            studentRoomCheckMetaData2.setValue("127.210157,45.743361;127.213485,45.74255;127.21364,45.739923;127.209396,45.740759");
            studentRoomCheckMetaData2.setBelongCounselorId(counselorId);
            studentRoomCheckMetaDataDao.save(studentRoomCheckMetaData2);
        }
    }

    @Override
    public boolean isNowCanRoomCheck(LoginUser loginUser) {
        StudentUser studentUser = infoClient.getStudentUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> new UserNameDoesNotExistException("用户不存在", HttpStatus.NOT_FOUND));
        StudentRoomCheckMetaData studentRoomCheckMetaData = studentRoomCheckMetaDataDao.findByKeyAndBelongCounselorId(StudentRoomCheckMetaData.KEY_ROOM_CHECK_TIME, studentUser.getBelongCounselorId());
        if (studentRoomCheckMetaData == null) {
            initAllKey(studentUser.getBelongCounselorId());
            studentRoomCheckMetaData = studentRoomCheckMetaDataDao.findByKeyAndBelongCounselorId(StudentRoomCheckMetaData.KEY_ROOM_CHECK_TIME, studentUser.getBelongCounselorId());
        }
        String dataValue = studentRoomCheckMetaData.getValue();
        String[] strings = dataValue.split(":");
        if (strings.length != TIME_SEPARATOR_ARRAY_LENGTH) {
            return false;
        }
        int ha = Integer.parseInt(strings[0]);
        int hb = Integer.parseInt(strings[1]);
        Calendar calendar = Calendar.getInstance();
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        if (h > ha) {
            return true;
        } else if (h < ha) {
            return false;
        } else {
            return m > hb;
        }
    }

    @Override
    public Date getStudentCheckDate(LoginUser loginUser) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> new UserNameDoesNotExistException("用户不存在", HttpStatus.NOT_FOUND));
        StudentRoomCheckMetaData studentRoomCheckMetaData = studentRoomCheckMetaDataDao.findByKeyAndBelongCounselorId(StudentRoomCheckMetaData.KEY_ROOM_CHECK_TIME, user.getId());
        if (studentRoomCheckMetaData == null) {
            initAllKey(user.getId());
            studentRoomCheckMetaData = studentRoomCheckMetaDataDao.findByKeyAndBelongCounselorId(StudentRoomCheckMetaData.KEY_ROOM_CHECK_TIME, user.getId());
        }
        try {
            int[] timeArray = Arrays.stream(studentRoomCheckMetaData.getValue().split(":")).mapToInt(Integer::parseInt).toArray();
            return localDateTime2Date(LocalTime.of(timeArray[0], timeArray[1]).atDate(LocalDate.of(2001, 1, 1)));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<List<Double>> getGpsRange(LoginUser loginUser, boolean isStudentLogin) {
        String counselorId;
        if (isStudentLogin) {
            counselorId = infoClient.getStudentUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> new UserNameDoesNotExistException("用户不存在", HttpStatus.NOT_FOUND)).getBelongCounselorId();
        } else {
            counselorId = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> new UserNameDoesNotExistException("用户不存在", HttpStatus.NOT_FOUND)).getId();
        }
        StudentRoomCheckMetaData studentRoomCheckMetaData = studentRoomCheckMetaDataDao.findByKeyAndBelongCounselorId(StudentRoomCheckMetaData.KEY_ROOM_CHECK_GPS_RANGE, counselorId);
        if (studentRoomCheckMetaData == null) {
            initAllKey(counselorId);
            studentRoomCheckMetaData = studentRoomCheckMetaDataDao.findByKeyAndBelongCounselorId(StudentRoomCheckMetaData.KEY_ROOM_CHECK_GPS_RANGE, counselorId);
        }
        String value = studentRoomCheckMetaData.getValue();
        return Arrays
                .stream(value.split(";"))
                .map(mapString2List())
                .collect(Collectors.toList());
    }

    @Override
    public String upStudentCheckDate(String dateString, LoginUser loginUser) {
        if (dateString.split(TIME_SEPARATOR).length != TIME_SEPARATOR_ARRAY_LENGTH) {
            throw new AppMetaException("时间错误", HttpStatus.BAD_REQUEST);
        }
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> new UserNameDoesNotExistException("用户不存在", HttpStatus.NOT_FOUND));
        StudentRoomCheckMetaData studentRoomCheckMetaData = studentRoomCheckMetaDataDao.findByKeyAndBelongCounselorId(StudentRoomCheckMetaData.KEY_ROOM_CHECK_TIME, user.getId());
        if (studentRoomCheckMetaData == null) {
            initAllKey(user.getId());
            studentRoomCheckMetaData = studentRoomCheckMetaDataDao.findByKeyAndBelongCounselorId(StudentRoomCheckMetaData.KEY_ROOM_CHECK_TIME, user.getId());
        }
        studentRoomCheckMetaData.setValue(dateString);
        return studentRoomCheckMetaDataDao.save(studentRoomCheckMetaData).getValue();
    }

    @Override
    public List<List<Double>> upGpsRange(String gps, LoginUser loginUser) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> new UserNameDoesNotExistException("用户不存在", HttpStatus.NOT_FOUND));
        StudentRoomCheckMetaData studentRoomCheckMetaData = studentRoomCheckMetaDataDao.findByKeyAndBelongCounselorId(StudentRoomCheckMetaData.KEY_ROOM_CHECK_GPS_RANGE, user.getId());
        if (studentRoomCheckMetaData == null) {
            initAllKey(user.getId());
            studentRoomCheckMetaData = studentRoomCheckMetaDataDao.findByKeyAndBelongCounselorId(StudentRoomCheckMetaData.KEY_ROOM_CHECK_GPS_RANGE, user.getId());
        }
        studentRoomCheckMetaData.setValue(gps);
        StudentRoomCheckMetaData saved = studentRoomCheckMetaDataDao.save(studentRoomCheckMetaData);
        return Arrays
                .stream(saved.getValue().split(";"))
                .map(mapString2List())
                .collect(Collectors.toList());
    }

    /**
     * 将String用<code>,</code>分割并放入List
     *
     * @return Function
     */
    private Function<String, List<Double>> mapString2List() {
        return a -> {
            String[] split = a.split(",");
            List<Double> list = new ArrayList<>();
            list.add(Double.parseDouble(split[0]));
            list.add(Double.parseDouble(split[1]));
            return list;
        };
    }
}
