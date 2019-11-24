package top.itning.smp.smproom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smproom.entity.StudentRoomCheck;
import top.itning.smp.smproom.security.LoginUser;

import java.io.IOException;
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
     * @param whereDay 哪天
     * @return 打卡信息
     */
    List<StudentRoomCheck> checkAll(Date whereDay);
}
