package top.itning.smp.smpclass.security;

import java.lang.annotation.*;

/**
 * @author itning
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MustLogin
public @interface MustCounselorLogin {
}
