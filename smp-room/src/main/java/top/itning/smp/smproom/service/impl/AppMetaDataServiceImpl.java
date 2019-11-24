package top.itning.smp.smproom.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smproom.dao.AppMetaDataDao;
import top.itning.smp.smproom.entity.AppMetaData;
import top.itning.smp.smproom.exception.AppMetaException;
import top.itning.smp.smproom.service.AppMetaDataService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppMetaDataServiceImpl implements AppMetaDataService {
    private static final ThreadLocal<SimpleDateFormat> simpleDateFormatThreadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("HH:mm"));
    private final AppMetaDataDao appMetaDataDao;

    @Autowired
    public AppMetaDataServiceImpl(AppMetaDataDao appMetaDataDao) {
        this.appMetaDataDao = appMetaDataDao;
        if (!appMetaDataDao.existsById(AppMetaData.KEY_ROOM_CHECK_TIME)) {
            AppMetaData appMetaData = new AppMetaData(AppMetaData.KEY_ROOM_CHECK_TIME, "20:30", null, null);
            appMetaDataDao.save(appMetaData);
        }
    }

    @Override
    public boolean isNowCanRoomCheck() {
        AppMetaData appMetaData = appMetaDataDao.findById(AppMetaData.KEY_ROOM_CHECK_TIME).orElseThrow(() -> new AppMetaException("归寝时间不存在", HttpStatus.NOT_FOUND));
        String dataValue = appMetaData.getValue();
        String[] strings = dataValue.split(":");
        if (strings.length != 2) {
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
    public Date getStudentCheckDate() {
        AppMetaData appMetaData = appMetaDataDao.findById(AppMetaData.KEY_ROOM_CHECK_TIME).orElseThrow(() -> new AppMetaException("归寝时间不存在", HttpStatus.NOT_FOUND));
        try {
            return simpleDateFormatThreadLocal.get().parse(appMetaData.getValue());
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public List<List<Double>> getGpsRange() {
        AppMetaData appMetaData = appMetaDataDao.findById(AppMetaData.KEY_ROOM_CHECK_GPS_RANGE).orElseThrow(() -> new AppMetaException("GPS范围不存在", HttpStatus.NOT_FOUND));
        //127.210157,45.743361;127.213485,45.74255;127.21364,45.739923;127.209396,45.740759
        String value = appMetaData.getValue();
        return Arrays
                .stream(value.split(";"))
                .map(latlo -> {
                    String[] split = latlo.split(",");
                    List<Double> list = new ArrayList<>();
                    list.add(Double.parseDouble(split[0]));
                    list.add(Double.parseDouble(split[1]));
                    return list;
                })
                .collect(Collectors.toList());
    }

    @Override
    public String upStudentCheckDate(String dateString) {
        if (dateString.split(":").length != 2) {
            throw new AppMetaException("时间错误", HttpStatus.BAD_REQUEST);
        }
        AppMetaData appMetaData = appMetaDataDao.findById(AppMetaData.KEY_ROOM_CHECK_TIME).orElse(new AppMetaData(AppMetaData.KEY_ROOM_CHECK_TIME, dateString, null, null));
        appMetaData.setValue(dateString);
        return appMetaDataDao.save(appMetaData).getValue();
    }

    @Override
    public List<List<Double>> upGpsRange(String gps) {
        AppMetaData appMetaData = appMetaDataDao.findById(AppMetaData.KEY_ROOM_CHECK_GPS_RANGE).orElse(new AppMetaData(AppMetaData.KEY_ROOM_CHECK_TIME, gps, null, null));
        appMetaData.setValue(gps);
        AppMetaData saved = appMetaDataDao.save(appMetaData);
        return Arrays
                .stream(saved.getValue().split(";"))
                .map(latlo -> {
                    String[] split = latlo.split(",");
                    List<Double> list = new ArrayList<>();
                    list.add(Double.parseDouble(split[0]));
                    list.add(Double.parseDouble(split[1]));
                    return list;
                })
                .collect(Collectors.toList());
    }
}
