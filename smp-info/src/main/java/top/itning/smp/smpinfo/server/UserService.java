package top.itning.smp.smpinfo.server;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.itning.smp.smpinfo.dto.StudentUserDTO;

/**
 * @author itning
 */
public interface UserService {
    /**
     * 获取所有用户
     *
     * @param pageable 分页信息
     * @return 用户
     */
    Page<StudentUserDTO> getAllUser(Pageable pageable);

    /**
     * 模糊搜索用户
     *
     * @param key      关键字
     * @param pageable 分页数据
     * @return 用户
     */
    Page<StudentUserDTO> searchUsers(String key, Pageable pageable);
}
