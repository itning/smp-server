package top.itning.smp.smproom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smproom.entity.StudentRoomCheck;
import top.itning.smp.smproom.security.LoginUser;

import java.io.IOException;

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
     * @return 打卡信息
     * @throws IOException 保存文件可能出现的异常
     */
    StudentRoomCheck check(MultipartFile file, LoginUser loginUser) throws IOException;
}
