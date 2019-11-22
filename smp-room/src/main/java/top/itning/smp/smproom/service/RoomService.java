package top.itning.smp.smproom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.itning.smp.smproom.entity.StudentRoomCheck;

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
}
