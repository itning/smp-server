package top.itning.smp.smpsecurity.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import top.itning.smp.smpsecurity.security.LoginUser;

/**
 * @author itning
 */
public interface SecurityService {
    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return TOKEN
     * @throws JsonProcessingException see {@link JsonProcessingException}
     */
    String login(String username, String password) throws JsonProcessingException;

    /**
     * 修改密码
     *
     * @param loginUser   登录用户
     * @param newPassword 新密码
     */
    void changePwd(LoginUser loginUser, String newPassword);

    /**
     * 重置密码
     *
     * @param loginUser 登录用户
     * @param studentId 学生ID
     */
    void resetPwd(LoginUser loginUser, String studentId);
}
