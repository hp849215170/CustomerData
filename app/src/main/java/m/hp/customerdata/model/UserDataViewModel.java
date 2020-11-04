package m.hp.customerdata.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import m.hp.customerdata.entity.MessageBean;
import m.hp.customerdata.room.UserDataRepository;

public class UserDataViewModel extends AndroidViewModel {

    private LiveData<List<MessageBean>> allUserData;
    private UserDataRepository mRepository;


    public UserDataViewModel(@NonNull Application application) {
        super(application);
        mRepository = new UserDataRepository(application);
        allUserData = mRepository.getAllUserData();
    }

    //获取全部数据
    public LiveData<List<MessageBean>> getAllUserData() {
        return allUserData;
    }

    //添加数据
    public void insert(MessageBean bean) {
        mRepository.insert(bean);
    }

    //删除一条数据
    public void delByName(String username) {
        mRepository.delByName(username);
    }

    //更新数据
    public int updateData(MessageBean bean) {
        return mRepository.updateData(bean);
    }

    /**
     * 通过名字查
     *
     * @param userName
     */
    public MessageBean getDataByUserName(String userName) {
        return mRepository.getDataByName(userName);
    }
}
