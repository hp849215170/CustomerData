package m.hp.customerdata.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author huangping
 */
public class MySharedPreferenceUtils {
    private final SharedPreferences sp;
    private final SharedPreferences.Editor edit;

    @SuppressLint("CommitPrefEdits")
    public MySharedPreferenceUtils(Context context, String spName, int mode) {
        sp = context.getSharedPreferences(spName, mode);
        edit = sp.edit();
    }

    public void savespboolean(String key, boolean b) {
        edit.putBoolean(key, b).commit();
    }

    public void savespstring(String key, String value) {
        edit.putString(key, value).commit();
    }

    public boolean getspboolean(String key, boolean b) {
        return sp.getBoolean(key, b);
    }

    public String getspstring(String key, String value) {
        return sp.getString(key, value);
    }

}
