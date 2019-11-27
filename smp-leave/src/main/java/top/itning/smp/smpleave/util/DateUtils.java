package top.itning.smp.smpleave.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author itning
 */
public class DateUtils {
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date11);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date11 = calendar.getTime();
        calendar.setTime(date12);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        date12 = calendar.getTime();
        calendar.setTime(date21);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date21 = calendar.getTime();
        calendar.setTime(date22);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        date22 = calendar.getTime();
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
}
