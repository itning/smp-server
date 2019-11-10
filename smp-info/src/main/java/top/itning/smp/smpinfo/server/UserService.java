package top.itning.smp.smpinfo.server;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smpinfo.dto.StudentUserDTO;
import top.itning.smp.smpinfo.dto.UpFileDTO;

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

    /**
     * 更新用户
     *
     * @param studentUserDTO 用户
     */
    void updateUser(StudentUserDTO studentUserDTO);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void delUser(String userId);

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 上传信息
     */
    UpFileDTO upFile(MultipartFile file);
}
