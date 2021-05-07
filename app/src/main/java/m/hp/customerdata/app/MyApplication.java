package m.hp.customerdata.app;

import android.app.Application;
import android.util.Log;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

/**
 * @author huangping
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initBugly();
    }

    public void initBugly() {
        Bugly.init(getApplicationContext(), "6129f2b727", false);
        Log.d("MyApplication", "注册推送成功");
        Beta.autoCheckAppUpgrade = true;
    }

}
