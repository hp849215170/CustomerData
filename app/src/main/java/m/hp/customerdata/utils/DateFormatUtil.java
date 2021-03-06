package m.hp.customerdata.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

/**
 * @author huangping
 */
public class DateFormatUtil {

    /**
     * 将时间转换成自定义格式
     *
     * @param date    日期
     * @param pattern 格式
     */
    public static String getFormatDate(String date, String pattern) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(Objects.requireNonNull(format.parse(date)));
    }

    /**
     * 获取当前日期
     */
    public static String getCurrentDate() {
        String currentDate;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        currentDate = year + "/" + month + "/" + dayOfMonth;
        return currentDate;
    }
}
