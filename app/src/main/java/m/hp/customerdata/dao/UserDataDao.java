package m.hp.customerdata.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import m.hp.customerdata.entity.UsersDataBean;

/**
 * @author huangping
 */
@Dao
public interface UserDataDao {
    /**
     * 插入数据
     *
     * @param bean 用户数据实体
     */
    @Insert
    void insert(UsersDataBean bean);

    /**
     * 删除所有数据
     * @return  删除的数据数量
     */
    @Query("DELETE FROM users_table")
    int deleteAll();

    /**
     * 根据名字删除数据
     * @param username 投保人
     */
    @Query("delete from users_table where user_name = :username")
    void delByName(String username);

    /**
     * 查询users_table表中所有数据
     * @return 所有数据
     */
    @Query("SELECT * FROM users_table")
    LiveData<List<UsersDataBean>> getAllUserData();

    /**
     * 通过投保人姓名模糊查询
     * @param userName 投保人名字
     * @return 通过名字模糊查询到的数据
     */
    @Query("select * from users_table where user_name like :userName||'%'")
    LiveData<List<UsersDataBean>> getDataByUserName(String userName);

    /**
     * 更新数据
     * @param bean 用户数据实体
     * @return 更新的数据数量
     */
    @Update
    int updateData(UsersDataBean bean);

    /**
     * 通过终保日期查
     * @param lastDate 终保日期
     * @return 通过终保日期查询的所有数据
     */
    @Query("select * from users_table where last_date=:lastDate")
    LiveData<List<UsersDataBean>> getLastDateUsers(String lastDate);

    /**
     * 按月查询数据
     * @param lastDate 终保日期
     * @return 按月查询到的所有数据
     */
    @Query("select * from users_table where last_date like '%/'||:lastDate||'/%'")
    LiveData<List<UsersDataBean>> getUsersByMonth(String lastDate);
}
