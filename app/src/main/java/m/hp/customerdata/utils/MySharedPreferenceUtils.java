package m.hp.customerdata.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferenceUtils {
    private SharedPreferences sp;
    private SharedPreferences.Editor edit;

    public MySharedPreferenceUtils(Context context, String spName, int mode) {
        sp = context.getSharedPreferences(spName, mode);
        edit = sp.edit();
    }

    public void saveSPBoolean(String key, boolean b) {
        edit.putBoolean(key, b).commit();
    }

    public void saveSPString(String key, String value) {
        edit.putString(key, value).commit();
    }

    public boolean getSPBoolean(String key, boolean b) {
        return sp.getBoolean(key, b);
    }

    public String getSPString(String key, String value) {
        return sp.getString(key, value);
    }

}
