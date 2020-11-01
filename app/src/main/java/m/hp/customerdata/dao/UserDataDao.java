package m.hp.customerdata.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import m.hp.customerdata.entity.MessageBean;

@Dao
public interface UserDataDao {
    @Insert
    void insert(MessageBean bean);//插入数据

    @Query("DELETE FROM users_table")
    int deleteAll();//删除所有数据

    @Query("delete from users_table where user_name = :username")
    int delByName(String username);

    @Query("SELECT * FROM users_table")
    LiveData<List<MessageBean>> getAllUserData();//查询users_table表中所有数据

    @Update(entity = MessageBean.class)
    int updater(MessageBean bean);

}
