package top.itning.smp.smproom.service;

import java.util.Date;
import java.util.List;

/**
 * @author itning
 */
public interface AppMetaDataService {
    /**
     * 现在可以进行学生寝室打卡
     *
     * @return 可以返回<code>true</code>
     */
    boolean isNowCanRoomCheck();

    /**
     * 获取学生寝室允许打卡时间
     *
     * @return 寝室允许打卡时间
     */
    Date getStudentCheckDate();

    /**
     * 获取GPS打卡范围
     *
     * @return 打卡范围
     */
    List<List<Double>> getGpsRange();

    /**
     * 更新学生寝室允许打卡时间
     *
     * @param dateString 打卡时间
     * @return 打卡时间
     */
    String upStudentCheckDate(String dateString);

    /**
     * 修改允许打卡GPS坐标范围信息
     *
     * @param gps GPS坐标范围信息
     * @return GPS坐标范围信息
     */
    List<List<Double>> upGpsRange(String gps);
}
