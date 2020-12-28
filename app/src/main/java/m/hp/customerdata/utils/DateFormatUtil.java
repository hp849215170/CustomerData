package m.hp.customerdata.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateFormatUtil {

    /**
     * 将时间转换成自定义格式
     * @param date 日期
     * @param pattern 格式
     */
    public static String getFormatDate(String date, String pattern) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(format.parse(date));
    }
}
