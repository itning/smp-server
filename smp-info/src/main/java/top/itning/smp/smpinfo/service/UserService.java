package top.itning.smp.smpinfo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smpinfo.dto.StudentUserDTO;
import top.itning.smp.smpinfo.dto.UpFileDTO;
import top.itning.smp.smpinfo.entity.User;
import top.itning.smp.smpinfo.security.LoginUser;

import java.io.IOException;
import java.util.List;

/**
 * @author itning
 */
public interface UserService {
    /**
     * 获取所有用户
     *
     * @param pageable  分页信息
     * @param loginUser 登录用户
     * @return 用户
     */
    Page<StudentUserDTO> getAllUser(Pageable pageable, LoginUser loginUser);

    /**
     * 模糊搜索用户
     *
     * @param key       关键字
     * @param pageable  分页数据
     * @param loginUser 登录用户
     * @return 用户
     */
    Page<StudentUserDTO> searchUsers(String key, Pageable pageable, LoginUser loginUser);

    /**
     * 更新用户
     *
     * @param studentUserDTO 用户
     * @param loginUser      登录用户
     */
    void updateUser(StudentUserDTO studentUserDTO, LoginUser loginUser);

    /**
     * 删除用户
     *
     * @param userId    用户ID
     * @param loginUser 登录用户
     */
    void delUser(String userId, LoginUser loginUser);

    /**
     * 上传文件
     *
     * @param file      文件
     * @param loginUser 登录用户
     * @return 上传信息
     * @throws IOException 存储异常
     */
    UpFileDTO upFile(MultipartFile file, LoginUser loginUser) throws IOException;

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户
     */
    User getUserInfoByUserName(String username);

    /**
     * 获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    StudentUserDTO getStudentUserInfoByUserName(String username);

    /**
     * 计算学生数量
     *
     * @param username 导员用户名
     * @return 学生数量
     */
    long countStudent(String username);

    /**
     * 获取所有用户
     *
     * @param username 导员用户名
     * @return 所有用户
     */
    List<StudentUserDTO> getAllUser(String username);

    /**
     * 根据学号获取学生DTO
     *
     * @param studentId 学号
     * @return 学生信息DTO
     */
    StudentUserDTO getStudentUserDtoByStudentId(String studentId);
}
