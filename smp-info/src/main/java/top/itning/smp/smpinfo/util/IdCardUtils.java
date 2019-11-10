package top.itning.smp.smpinfo.util;

import com.google.common.collect.Maps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 身份证工具类
 *
 * @author itning
 */
public class IdCardUtils {
    /**
     * CN 大陆第二代身份证号长度
     */
    private static final int ID_CARD_LENGTH = 18;

    /**
     * 解析身份证号码
     *
     * @param idCardNum 身份证号
     * @return Map
     */
    public static Map<String, Object> analysisIdCard(String idCardNum) {
        if (idCardNum.length() != ID_CARD_LENGTH) {
            throw new IllegalArgumentException("身份证号长度不是18位，请检查");
        }

        Map<String, Object> map = Maps.newHashMapWithExpectedSize(3);
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            Date birthday = simpleDateFormat.parse(idCardNum.substring(6, 14));
            map.put("birthday", birthday);
            boolean sex = Integer.parseInt(idCardNum.substring(16, 17)) % 2 != 0;
            map.put("sex", sex);
            Calendar cal = Calendar.getInstance();
            int age = cal.get(Calendar.YEAR) - Integer.parseInt(idCardNum.substring(6, 10));
            map.put("age", age);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
}
