package top.itning.smp.smpstatistics.aop;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.itning.smp.smpstatistics.security.LoginUser;
import top.itning.smp.smpstatistics.util.DateUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static top.itning.smp.smpstatistics.util.DateUtils.ZONE_ID;

/**
 * @author itning
 */
@Aspect
@Component
public class CacheAop {
    private static final Logger logger = LoggerFactory.getLogger(CacheAop.class);
    private static final int METHOD_PARAMS_LENGTH = 2;
    private static final String NO_NEED_ADD_LOGIN_USER_KEY_METHOD_NAME = "getClassComingChart";
    private static final Cache<String, Object> CACHE;

    static {
        CACHE = CacheBuilder
                .newBuilder()
                .maximumSize(100)
                // 10min to remove
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .weakValues()
                .build();
    }

    @Pointcut("execution(* top.itning.smp.smpstatistics.service.impl.StatisticsServiceImpl.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        final int length = joinPoint.getArgs().length;
        if (length == 0) {
            return joinPoint.proceed();
        }
        if (length == METHOD_PARAMS_LENGTH) {
            Date date = (Date) joinPoint.getArgs()[0];
            LoginUser loginUser = (LoginUser) joinPoint.getArgs()[1];
            // 实时刷新的不要缓存
            if (!DateUtils.date2LocalDateTime(date).toLocalDate().equals(LocalDate.now(ZONE_ID))) {
                String key;
                if (NO_NEED_ADD_LOGIN_USER_KEY_METHOD_NAME.equals(joinPoint.getSignature().getName())) {
                    key = joinPoint.getSignature().toString() + date.getTime();
                } else {
                    key = joinPoint.getSignature().toString() + loginUser.toString() + date.getTime();
                }
                return getObject(joinPoint, key);
            }
            return joinPoint.proceed();
        }
        Date start = (Date) joinPoint.getArgs()[1];
        Date end = (Date) joinPoint.getArgs()[2];
        String key = start.getTime() + "|" + end.getTime();
        return getObject(joinPoint, key);
    }

    private Object getObject(ProceedingJoinPoint joinPoint, String key) throws Throwable {
        Object o = CACHE.getIfPresent(key);
        if (o == null) {
            logger.debug("make cache {}", key);
            Object proceed = joinPoint.proceed();
            CACHE.put(key, proceed);
            return proceed;
        } else {
            return o;
        }
    }
}
