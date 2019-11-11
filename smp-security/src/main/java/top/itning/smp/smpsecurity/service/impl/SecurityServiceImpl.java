package top.itning.smp.smpsecurity.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smpsecurity.client.InfoClient;
import top.itning.smp.smpsecurity.client.entity.User;
import top.itning.smp.smpsecurity.entity.LoginUser;
import top.itning.smp.smpsecurity.exception.UserNameDoesNotExistException;
import top.itning.smp.smpsecurity.exception.UserPasswordException;
import top.itning.smp.smpsecurity.service.SecurityService;
import top.itning.smp.smpsecurity.util.JwtUtils;
import top.itning.smp.smpsecurity.util.OrikaUtils;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SecurityServiceImpl implements SecurityService {
    private final InfoClient infoClient;

    @Autowired
    public SecurityServiceImpl(InfoClient infoClient) {
        this.infoClient = infoClient;
    }

    @Override
    public String login(String username, String password) throws JsonProcessingException {
        // TODO 密码加密
        User user = infoClient.getUserInfoByUserName(username).orElseThrow(() -> new UserNameDoesNotExistException("用户名不存在", HttpStatus.NOT_FOUND));
        if (!user.getPassword().equals(password)) {
            throw new UserPasswordException("密码错误", HttpStatus.NOT_FOUND);
        }
        LoginUser loginUser = OrikaUtils.a2b(user, LoginUser.class);
        return JwtUtils.buildJwt(loginUser);
    }
}
