package m.hp.customerdata.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import m.hp.customerdata.entity.MessageBean;

@Dao
public interface UserDataDao {
    @Insert
    void insert(MessageBean bean);//插入数据

    @Query("DELETE FROM users_table")
    void deleteAll();//删除所有数据

    @Query("SELECT * FROM users_table")
    LiveData<List<MessageBean>> getAllUserData();//查询users_table表中所有数据
}
