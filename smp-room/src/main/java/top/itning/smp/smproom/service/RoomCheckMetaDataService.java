package top.itning.smp.smproom.service;

import top.itning.smp.smproom.security.LoginUser;

import java.util.Date;
import java.util.List;

/**
 * @author itning
 */
public interface RoomCheckMetaDataService {
    /**
     * 现在可以进行学生寝室打卡
     *
     * @param loginUser 登录用户
     * @return 可以返回<code>true</code>
     */
    boolean isNowCanRoomCheck(LoginUser loginUser);

    /**
     * 获取学生寝室允许打卡时间
     *
     * @param loginUser 登录用户
     * @return 寝室允许打卡时间
     */
    Date getStudentCheckDate(LoginUser loginUser);

    /**
     * 获取GPS打卡范围
     *
     * @param loginUser      登录用户
     * @param isStudentLogin 学生登录？
     * @return 打卡范围
     */
    List<List<Double>> getGpsRange(LoginUser loginUser, boolean isStudentLogin);

    /**
     * 更新学生寝室允许打卡时间
     *
     * @param dateString 打卡时间
     * @param loginUser  登录用户
     * @return 打卡时间
     */
    String upStudentCheckDate(String dateString, LoginUser loginUser);

    /**
     * 修改允许打卡GPS坐标范围信息
     *
     * @param gps       GPS坐标范围信息
     * @param loginUser 登录用户
     * @return GPS坐标范围信息
     */
    List<List<Double>> upGpsRange(String gps, LoginUser loginUser);
}
