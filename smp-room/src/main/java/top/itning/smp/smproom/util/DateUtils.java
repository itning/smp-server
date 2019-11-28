package top.itning.smp.smproom.util;

import top.itning.utils.tuple.Tuple2;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author itning
 */
public class DateUtils {
    /**
     * 北京时区
     */
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    public static final DateTimeFormatter YYYYMMDD_DATE_TIME_FORMATTER_1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter YYYYMMDDHHMMSS_DATE_TIME_FORMATTER_2 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    /**
     * 将{@link Date}转换为{@link LocalDateTime}
     *
     * @param date {@link Date}
     * @return {@link LocalDateTime}
     */
    private static LocalDateTime date2LocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZONE_ID);
    }

    /**
     * 将{@link LocalDateTime}转换为{@link Date}
     *
     * @param localDateTime {@link LocalDateTime}
     * @return {@link Date}
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZONE_ID).toInstant());
    }

    /**
     * 将时间重置为0点
     *
     * @param localDateTime {@link LocalDateTime}
     * @return {@link LocalDateTime}
     */
    private static LocalDateTime with0Time(LocalDateTime localDateTime) {
        return localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * 获取startDate的0点到第二天0点
     * 与输入的startDate时间无关
     *
     * @param startDate 开始日期
     * @return 元组
     */
    public static Tuple2<Date, Date> getDateRange(Date startDate) {
        LocalDateTime localDateTime = with0Time(date2LocalDateTime(startDate));
        Date t1 = localDateTime2Date(localDateTime);
        Date t2 = localDateTime2Date(localDateTime.plusDays(1));
        return new Tuple2<>(t1, t2);
    }

    public static String format(Date date, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.ofInstant(date.toInstant(), ZONE_ID).format(dateTimeFormatter);
    }
}
