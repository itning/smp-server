package top.itning.smp.smpstatistics.security;

import java.lang.annotation.*;

/**
 * @author itning
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MustLogin(role = MustLogin.ROLE.TEACHER)
public @interface MustTeacherLogin {
}
