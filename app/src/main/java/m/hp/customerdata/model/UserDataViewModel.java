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

    public LiveData<List<MessageBean>> getAllUserData() {
        return allUserData;
    }

    public void insert(MessageBean bean) {
        mRepository.insert(bean);
    }
}
