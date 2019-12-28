package top.itning.smp.smpsecurity.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import top.itning.smp.smpsecurity.client.InfoClient;
import top.itning.smp.smpsecurity.client.entity.StudentUserDTO;
import top.itning.smp.smpsecurity.client.entity.User;
import top.itning.smp.smpsecurity.entity.LoginUser;
import top.itning.smp.smpsecurity.exception.TokenException;
import top.itning.smp.smpsecurity.exception.UserNameDoesNotExistException;
import top.itning.smp.smpsecurity.exception.UserPasswordException;
import top.itning.smp.smpsecurity.service.SecurityService;
import top.itning.smp.smpsecurity.util.JwtUtils;
import top.itning.smp.smpsecurity.util.OrikaUtils;

/**
 * @author itning
 */
@Service
public class SecurityServiceImpl implements SecurityService {
    private final InfoClient infoClient;

    @Autowired
    public SecurityServiceImpl(InfoClient infoClient) {
        this.infoClient = infoClient;
    }

    @Override
    public String login(String username, String password) throws JsonProcessingException {
        User user = infoClient.getUserInfoByUserName(username).orElseThrow(() -> new UserNameDoesNotExistException("用户名不存在", HttpStatus.NOT_FOUND));
        if (!user.getPassword().equals(password)) {
            throw new UserPasswordException("密码错误", HttpStatus.NOT_FOUND);
        }
        LoginUser loginUser = OrikaUtils.a2b(user, LoginUser.class);
        return JwtUtils.buildJwt(loginUser);
    }

    @Override
    public void changePwd(top.itning.smp.smpsecurity.security.LoginUser loginUser, String newPassword) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> new UserNameDoesNotExistException("用户名不存在", HttpStatus.NOT_FOUND));
        if (newPassword.equals(user.getPassword())) {
            throw new UserPasswordException("新密码不能和旧密码相同", HttpStatus.BAD_REQUEST);
        }
        boolean success = infoClient.changeUserPwd(user.getUsername(), newPassword);
        if (!success) {
            throw new UserPasswordException("更改失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void resetPwd(top.itning.smp.smpsecurity.security.LoginUser loginUser, String studentId) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> new UserNameDoesNotExistException("辅导员未找到", HttpStatus.NOT_FOUND));
        StudentUserDTO studentUserDto = infoClient.getStudentUserDtoByStudentId(studentId);
        if (studentUserDto == null) {
            throw new UserNameDoesNotExistException("学号不存在", HttpStatus.NOT_FOUND);
        }
        if (!studentUserDto.getBelongCounselorId().equals(user.getId())) {
            throw new TokenException("重置失败，该学生不是您的学生", HttpStatus.FORBIDDEN);
        }
        boolean success = infoClient.changeUserPwd(studentUserDto.getUsername(), studentUserDto.getName());
        if (!success) {
            throw new UserPasswordException("重置失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
