package m.hp.customerdata.room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    //更新数据
    public int updateData(MessageBean bean) {
        Future<Integer> result_future = UserDataRoomDatabase.databaseWriteExecutor.submit((Callable<Integer>) () ->
                mUserDataDao.updateData(bean));
        try {
            return result_future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
       /* UserDataRoomDatabase.databaseWriteExecutor.execute(() -> {
            int result;
            result = mUserDataDao.updateData(bean);
            Log.e("info-->", "db update result:" + result);
        });*/
    }

    /**
     * 通过名字查询
     *
     * @param userName 投保人名字
     * @return
     */
    public MessageBean getDataByName(String userName) {
        //线程池执行返回结果
        Future<MessageBean> beanFuture = UserDataRoomDatabase.databaseWriteExecutor.submit(() ->
                mUserDataDao.getDataByUserName(userName));
        try {
            return beanFuture.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
