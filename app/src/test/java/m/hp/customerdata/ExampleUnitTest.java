package m.hp.customerdata;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import m.hp.customerdata.entity.UsersDataBean;
import m.hp.customerdata.utils.DateFormatUtil;
import m.hp.customerdata.utils.MyCompareUtil;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void textRegex() {
        String str = "Z1123AT89AVC777";

        Pattern p = Pattern.compile("^[A-Z][0-9]");
        Matcher m = p.matcher(str);
        boolean b = m.find();
        System.out.println(b);
    }


    @Test
    public void textUserNameCompare() {
        // 新建ArrayList(动态数组)
        ArrayList<UsersDataBean> list = new ArrayList<UsersDataBean>();
        // 添加对象到ArrayList中
        UsersDataBean bean = new UsersDataBean();
        bean.setUserName("郭德纲");
        list.add(bean);

        UsersDataBean bean2 = new UsersDataBean();
        bean2.setUserName("安华");
        list.add(bean2);

        UsersDataBean bean3 = new UsersDataBean();
        bean3.setUserName("赵紫阳");
        list.add(bean3);

        UsersDataBean bean4 = new UsersDataBean();
        bean4.setUserName("单田芳");
        list.add(bean4);

        for (int i = 0; i < list.size(); i++) {
            System.out.println("原始list顺序：" + list.get(i).getUserName());
        }

        Collections.sort(list, new MyCompareUtil(MyCompareUtil.SORT_DES, MyCompareUtil.COMPARE_NAME));
        for (int i = 0; i < list.size(); i++) {
            System.out.println("排序后的list：" + list.get(i).getUserName());
        }
    }

    @Test
    public void testCompareByDate() {
        // 新建ArrayList(动态数组)
        ArrayList<UsersDataBean> list = new ArrayList<UsersDataBean>();
        // 添加对象到ArrayList中
        UsersDataBean bean = new UsersDataBean();
        bean.setLastDate("2010/01/01");
        list.add(bean);

        UsersDataBean bean2 = new UsersDataBean();
        bean2.setLastDate("2010/10/01");
        list.add(bean2);

        UsersDataBean bean3 = new UsersDataBean();
        bean3.setLastDate("2010/05/01");
        list.add(bean3);

        UsersDataBean bean4 = new UsersDataBean();
        bean4.setLastDate("2010/08/01");
        list.add(bean4);

        for (int i = 0; i < list.size(); i++) {
            System.out.println("原始list顺序：" + list.get(i).getLastDate());
        }

        Collections.sort(list, new MyCompareUtil(MyCompareUtil.SORT_ASC, MyCompareUtil.COMPARE_DATE));
        for (int i = 0; i < list.size(); i++) {
            System.out.println("排序后的list：" + list.get(i).getLastDate());
        }
    }

    @Test
    public void testNotification() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/M/d");
        long timeMillis = System.currentTimeMillis();
        String lastDate = "2020/02/23";
        try {
            Date date = format.parse(lastDate);
            System.out.println("相差的时间===" + format.format(date));
            long day = date.getTime();
            double leftTime = ((day - timeMillis) / 1000.0 / 60 / 60 / 24);

            System.err.println("相差的时间===" + leftTime);
            if (leftTime < 30) {
                System.err.println("可以续保了");
            } else {

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExcelPOI() {
        System.out.println(10 ^ 6);
    }

    @Test
    public void testDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    }

    @Test
    public void testCurrentDate() {
        String currentDate = DateFormatUtil.getCurrentDate();
        System.out.println(currentDate);
    }
}