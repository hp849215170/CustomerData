package m.hp.customerdata.room;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import m.hp.customerdata.dao.UserDataDao;
import m.hp.customerdata.entity.MessageBean;

public class UserDataRepository {
    private UserDataDao mUserDataDao;
    private LiveData<List<MessageBean>> mAllUserData;

    public UserDataRepository(Application application) {
        //获取数据库连接
        UserDataRoomDatabase db = UserDataRoomDatabase.getDataBase(application);
        //拿到操作实体类
        mUserDataDao = db.userDataDao();
        //获取所有的数据集合
        mAllUserData = mUserDataDao.getAllUserData();
    }

    public LiveData<List<MessageBean>> getAllUserData() {
        return mAllUserData;
    }

    public void insert(MessageBean bean) {
        UserDataRoomDatabase.databaseWriteExecutor.execute(() -> mUserDataDao.insert(bean));
    }

    //按名字删除数据
    public void delByName(String username) {
        UserDataRoomDatabase.databaseWriteExecutor.execute(() -> {
            mUserDataDao.delByName(username);
        });
    }

    //通过名字更新数据
    public void updaterByName(MessageBean bean) {
        UserDataRoomDatabase.databaseWriteExecutor.execute(() -> {
            int result=mUserDataDao.updater(bean);
            Log.e("info-->","db update result:"+result);
        });
    }
}
