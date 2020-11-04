package m.hp.customerdata;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import m.hp.customerdata.entity.MessageBean;
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
        ArrayList<MessageBean> list = new ArrayList<MessageBean>();
        // 添加对象到ArrayList中
        MessageBean bean = new MessageBean();
        bean.setUserName("郭德纲");
        list.add(bean);

        MessageBean bean2 = new MessageBean();
        bean2.setUserName("安华");
        list.add(bean2);

        MessageBean bean3 = new MessageBean();
        bean3.setUserName("赵紫阳");
        list.add(bean3);

        MessageBean bean4 = new MessageBean();
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
        ArrayList<MessageBean> list = new ArrayList<MessageBean>();
        // 添加对象到ArrayList中
        MessageBean bean = new MessageBean();
        bean.setLastDate("2010/01/01");
        list.add(bean);

        MessageBean bean2 = new MessageBean();
        bean2.setLastDate("2010/10/01");
        list.add(bean2);

        MessageBean bean3 = new MessageBean();
        bean3.setLastDate("2010/05/01");
        list.add(bean3);

        MessageBean bean4 = new MessageBean();
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
}