package m.hp.customerdata.room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import m.hp.customerdata.dao.UserDataDao;
import m.hp.customerdata.entity.UsersDataBean;

public class UserDataRepository {
    private final UserDataDao mUserDataDao;
    private final LiveData<List<UsersDataBean>> mAllUserData;

    public UserDataRepository(Application application) {
        //获取数据库连接
        UserDataRoomDatabase db = UserDataRoomDatabase.getDataBase(application);
        //拿到操作实体类
        mUserDataDao = db.userDataDao();
        //获取所有的数据集合
        mAllUserData = mUserDataDao.getAllUserData();
    }

    public LiveData<List<UsersDataBean>> getAllUserData() {
        return mAllUserData;
    }

    public void insert(UsersDataBean bean) {
        UserDataRoomDatabase.databaseWriteExecutor.execute(() -> mUserDataDao.insert(bean));
    }

    //按名字删除数据
    public void delByName(String username) {
        UserDataRoomDatabase.databaseWriteExecutor.execute(() -> mUserDataDao.delByName(username));
    }

    //更新数据
    public void updateData(UsersDataBean bean) {
        Future<Integer> result_future = UserDataRoomDatabase.databaseWriteExecutor.submit((Callable<Integer>) () ->
                mUserDataDao.updateData(bean));
        try {
            result_future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过名字查询
     *
     * @param userName 投保人名字
     * @return 通过投保人姓名查询到的信息
     */
    public List<UsersDataBean> getDataByName(String userName) {
        //线程池执行返回结果
        Future<List<UsersDataBean>> beanFuture = UserDataRoomDatabase.databaseWriteExecutor.submit(() ->
                mUserDataDao.getDataByUserName(userName));
        try {
            return beanFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查终保日期数据
     *
     * @param lastDate 终保日期
     * @return 通过终保日期查询到的客户信息
     */
    public List<UsersDataBean> getLastDateUsers(String lastDate) {
        Future<List<UsersDataBean>> beanFuture = UserDataRoomDatabase.databaseWriteExecutor.submit(() ->
                mUserDataDao.getLastDateUsers(lastDate));
        try {
            return beanFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
