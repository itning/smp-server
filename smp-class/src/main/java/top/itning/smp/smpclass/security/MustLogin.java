package top.itning.smp.smpclass.security;

import java.lang.annotation.*;

/**
 * @author itning
 */
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MustLogin {
}
