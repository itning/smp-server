package top.itning.smp.smproom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smproom.entity.StudentRoomCheck;
import top.itning.smp.smproom.security.LoginUser;
import top.itning.utils.tuple.Tuple2;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * @author itning
 */
public interface RoomService {
    /**
     * 根据用户名获取寝室打卡信息
     *
     * @param username 用户
     * @param pageable 分页
     * @return 寝室打卡信息
     */
    Page<StudentRoomCheck> getRoomCheckInfoByStudentUserName(String username, Pageable pageable);

    /**
     * 寝室打卡
     *
     * @param file      文件
     * @param loginUser 登录用户
     * @param longitude 经度
     * @param latitude  纬度
     * @return 打卡信息
     * @throws IOException 保存文件可能出现的异常
     */
    StudentRoomCheck check(MultipartFile file, LoginUser loginUser, double longitude, double latitude) throws IOException;

    /**
     * 获取学生某天打卡信息
     * 例如输入 2019/11/23 14:32:32:235
     * 则范围为
     * 2019/11/23 00:00:00:000 - 2019/11/24 00:00:00:000
     *
     * @param whereDay  哪天
     * @param loginUser 登录用户
     * @return 打卡信息
     */
    List<StudentRoomCheck> checkAll(Date whereDay, LoginUser loginUser);

    /**
     * 计算应该打卡的学生数量
     *
     * @param date      日期
     * @param loginUser 登录用户
     * @return 应打卡学生数量 T1 学生总数 T2 请假生效中人数
     */
    Tuple2<Long, Long> countShouldRoomCheck(String date, LoginUser loginUser);

    /**
     * 导出Excel
     *
     * @param outputStream OutputStream
     * @param whereDay     日期
     * @param loginUser    登录用户
     * @throws IOException 导出异常
     */
    void export(OutputStream outputStream, Date whereDay, LoginUser loginUser) throws IOException;

    /**
     * 计算某天归寝人数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param username  导员用户名
     * @return 人数
     */
    long comingRoomCount(String username, Date startDate, Date endDate);
}
