package top.itning.smp.smpstatistics.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import top.itning.smp.smpstatistics.entity.Role;
import top.itning.smp.smpstatistics.exception.SecurityException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author itning
 */
@Component
public class SecurityHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private static final Logger logger = LoggerFactory.getLogger(SecurityHandlerMethodArgumentResolver.class);

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MustLogin.class) ||
                parameter.hasParameterAnnotation(MustCounselorLogin.class) ||
                parameter.hasParameterAnnotation(MustTeacherLogin.class) ||
                parameter.hasParameterAnnotation(MustStudentLogin.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        assert request != null;
        String roleId = request.getParameter("roleId");
        checkLoginPermission(parameter, roleId);
        LoginUser loginUser = new LoginUser();
        loginUser.setName(request.getParameter("name"));
        loginUser.setUsername(request.getParameter("username"));
        loginUser.setRoleId(request.getParameter(roleId));
        loginUser.setEmail(request.getParameter("email"));
        loginUser.setTel(request.getParameter("tel"));
        return loginUser;
    }

    private void checkLoginPermission(@NonNull MethodParameter parameter, String roleId) {
        if (parameter.hasParameterAnnotation(MustStudentLogin.class) &&
                !Role.STUDENT_ROLE_ID.equals(roleId)) {
            logger.debug("MustStudentLogin role id {}", roleId);
            throw new SecurityException("权限不足", HttpStatus.FORBIDDEN);
        }
        if (parameter.hasParameterAnnotation(MustTeacherLogin.class) &&
                !Role.TEACHER_ROLE_ID.equals(roleId)) {
            logger.debug("MustTeacherLogin role id {}", roleId);
            throw new SecurityException("权限不足", HttpStatus.FORBIDDEN);
        }
        if (parameter.hasParameterAnnotation(MustCounselorLogin.class) &&
                !Role.COUNSELOR_ROLE_ID.equals(roleId)) {
            logger.debug("MustCounselorLogin role id {}", roleId);
            throw new SecurityException("权限不足", HttpStatus.FORBIDDEN);
        }
        if (parameter.hasParameterAnnotation(MustLogin.class)) {
            MustLogin mustLogin = parameter.getParameterAnnotation(MustLogin.class);
            if (mustLogin != null) {
                if (Arrays.stream(mustLogin.role()).noneMatch(role -> role.getId().equals(roleId))) {
                    logger.debug("MustLogin role id {} and set role array {}", roleId, Arrays.toString(mustLogin.role()));
                    throw new SecurityException("权限不足", HttpStatus.FORBIDDEN);
                }
            }
        }
    }
}
