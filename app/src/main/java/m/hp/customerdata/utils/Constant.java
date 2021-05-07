package m.hp.customerdata.utils;

/**
 * @author HP-PC
 */
public class Constant {
    /**
     * 本地局域网
     */
    public static final String NATIVE_SERVER_URL = "http://192.168.1.166:8080/MyLocalServer/SyncUserDataServlet";
    /**
     * 腾讯轻量级无服务外网
     */
    public static final String TENCENT_LIGHT_SERVER_URL = "http://150.158.162.186:8080/MyLocalServer/SyncUserDataServlet";
    /**
     * 花生壳
     */
    public static final String PHDDNS_SERVER_URL = "http://286238c447.picp.vip/MyLocalServer/SyncUserDataServlet";
    /**
     * AddUserActivity常量
     */
    public static final String CAR_NUMBER = "车牌号";
    public static final String USER_NAME = "投保人";
    public static final String CAR_SERIAL_NUMBER = "车架号";
    public static final int CAR_SERIAL_NUMBER_LENGTH = 17;
    public static final String PHONE_NUMBER = "手机号";
    public static final int PHONE_NUMBER_LENGTH = 11;
    public static final String PHONE_NUMBER_START = "1";
    public static final String BUY_TIME = "承保时间";
    public static final String LAST_DATE = "终保时间";
    /**
     * DetailedActivity常量
     */
    public static final int TITLE_LENGTH = 15;
    /**
     * MainActivity常量
     */
    public static final int VIEWPAGER_FIRST = 0;
    public static final int VIEWPAGER_SECOND = 1;
    public static final int VIEWPAGER_THIRD = 2;
    /**
     * ShowDirectoryActivity常量
     */
    public static final String START_DIR = "/storage/emulated";
    /**
     *AddUserAdapter常量
     * */
    public static final String SY_PRICE = "商业险费用";
    public static final String JQ_PRICE = "交强险费用";
    public static final String JC_PRICE = "驾乘险费用";
    public static final String SY_REBATE = "商业险费率";
    public static final String JQ_REBATE = "交强险费率";
    public static final String JC_REBATE = "驾乘险费率";
    public static final String CASH_BACK = "返现";
    public static final String USER_TYPE = "客户来源";
    public static final String REMARK = "备注";

    /**
     *MysFragment常量
     * */
    public static final String HTML_START = "<html>";


    public static final String DOC_KEY_PRIMARY = "primary";
    public static final String DOC_KEY_CONTENT = "content";
    public static final String DOC_KEY_FILE = "file";
}
