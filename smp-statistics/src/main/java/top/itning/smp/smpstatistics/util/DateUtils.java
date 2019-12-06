package top.itning.smp.smpstatistics.util;

import top.itning.utils.tuple.Tuple2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author itning
 */
public class DateUtils {
    /**
     * 北京时区
     */
    public static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");
    /**
     * 最小时间
     */
    public static final Date MIN_DATE = Date.from(LocalDate.of(2001, 1, 1).atStartOfDay().atZone(ZONE_ID).toInstant());
    /**
     * 最大时间
     */
    public static final Date MAX_DATE = Date.from(LocalDate.of(9999, 12, 31).atTime(LocalTime.MAX).atZone(ZONE_ID).toInstant());

    /**
     * 判断两个时间区间是否有交集的方法
     *  
     *
     * @param date11            区间1的时间始
     * @param date12            区间1的时间止
     * @param date21            区间2的时间始
     * @param date22            区间2的时间止
     * @return 区间1和区间2如果存在交集, 则返回true, 否则返回falses
     */
    @SuppressWarnings("all")
    public static boolean isDateCross(Date date11, Date date12, Date date21, Date date22) {
        date11 = localDateTime2Date(with0Time(date2LocalDateTime(date11)));
        date12 = localDateTime2Date(with59Time(date2LocalDateTime(date12)));
        date21 = localDateTime2Date(with0Time(date2LocalDateTime(date21)));
        date22 = localDateTime2Date(with59Time(date2LocalDateTime(date22)));
        // 默认无交集
        boolean flag = false;
        long l1_1 = date11.getTime();
        long l1_2 = date12.getTime();
        long l2_1 = date21.getTime();
        long l2_2 = date22.getTime();

        if ((l1_1 > l1_2) || (l2_1 > l2_2)) {
            throw new RuntimeException("Parameter error:date11 should not be great than date12 and date21 should not be great than date22");
        }
        if (
                ((l1_1 <= l2_1) && (l2_1 <= l1_2)) ||
                        ((l1_1 <= l2_2) && (l2_2 <= l1_2)) ||
                        ((l2_1 <= l1_1) && (l1_1 <= l2_2)) ||
                        ((l2_1 <= l1_2) && (l1_2 <= l2_2))
        ) {
            flag = true;
        }
        return flag;
    }

    /**
     * 将{@link Date}转换为{@link LocalDateTime}
     *
     * @param date {@link Date}
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
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
    public static LocalDateTime with0Time(LocalDateTime localDateTime) {
        return localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public static LocalDateTime getNow() {
        return LocalDateTime.now(ZONE_ID);
    }

    public static LocalDateTime getNextDayFromNow() {
        return getNow().plusDays(1);
    }

    /**
     * 将时间重置为23点59分59秒
     *
     * @param localDateTime {@link LocalDateTime}
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime with59Time(LocalDateTime localDateTime) {
        return localDateTime.withHour(23).withMinute(59).withSecond(59).withNano(999);
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
}
