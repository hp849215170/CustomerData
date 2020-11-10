package m.hp.customerdata.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import m.hp.customerdata.entity.UsersDataBean;

@Dao
public interface UserDataDao {
    //插入数据
    @Insert
    void insert(UsersDataBean bean);

    //删除所有数据
    @Query("DELETE FROM users_table")
    int deleteAll();

    //根据名字删除数据
    @Query("delete from users_table where user_name = :username")
    int delByName(String username);

    //查询users_table表中所有数据
    @Query("SELECT * FROM users_table")
    LiveData<List<UsersDataBean>> getAllUserData();

    //通过名字模糊查数据
    @Query("select * from users_table where user_name like :userName||'%'")
    List<UsersDataBean> getDataByUserName(String userName);

    //更新数据
    @Update
    int updateData(UsersDataBean bean);

    //通过终保日期查
    @Query("select * from users_table where last_date=:lastDate")
    List<UsersDataBean> getLastDateUsers(String lastDate);
}
