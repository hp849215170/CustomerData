package m.hp.customerdata.utils;

import android.annotation.SuppressLint;

import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Locale;

import m.hp.customerdata.entity.UsersDataBean;

/**
 * 名字排序
 */
public class MyCompareUtil implements Comparator<UsersDataBean> {
    //按名字首字拼音排序
    public static final String COMPARE_NAME = "COMPARE_NAME";
    //按时间排序
    public static final String COMPARE_DATE = "COMPARE_DATE";
    //升序排序
    public static final int SORT_ASC = 0;
    //降序排序
    public static final int SORT_DES = 1;
    //排序顺序
    private final int sortFlag;
    //排序方式
    private final String compareType;

    public MyCompareUtil(int sortFlag, String compareType) {
        this.sortFlag = sortFlag;
        this.compareType = compareType;
    }

    @Override
    public int compare(UsersDataBean bean1, UsersDataBean bean2) {
        //名字排序
        if (compareType.equals(COMPARE_NAME)) {
            String userName1 = bean1.getUserName();
            String userName2 = bean2.getUserName();
            Collator collator = Collator.getInstance(Locale.CHINA);

            if (collator.compare(userName1, userName2) < 0) {
                if (sortFlag == SORT_ASC) {//升序排序
                    return -1;
                } else if (sortFlag == SORT_DES) {//降序排序
                    return 1;
                }
            } else if (collator.compare(userName1, userName2) > 0) {
                if (sortFlag == SORT_ASC) {//升序排序
                    return 1;
                } else if (sortFlag == SORT_DES) {//降序排序
                    return -1;
                }
            }
        } else if (compareType.equals(COMPARE_DATE)) {//时间排序
            @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");//设定时间格式
            String date1 = bean1.getLastDate();
            String date2 = bean2.getLastDate();

            try {
                if (date.parse(date1).getTime() - date.parse(date2).getTime() < 0) {
                    if (sortFlag == SORT_ASC) {//升序排序
                        return -1;
                    } else if (sortFlag == SORT_DES) {//降序排序
                        return 1;
                    }
                } else if (date.parse(date1).getTime() - date.parse(date2).getTime() > 0) {
                    if (sortFlag == SORT_ASC) {//升序排序
                        return 1;
                    } else if (sortFlag == SORT_DES) {//降序排序
                        return -1;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

}
