package top.itning.smp.smpsecurity.service;

import com.fasterxml.jackson.core.JsonProcessingException;

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
}
