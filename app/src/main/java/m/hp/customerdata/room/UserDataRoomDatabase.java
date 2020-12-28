package m.hp.customerdata.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import m.hp.customerdata.dao.UserDataDao;
import m.hp.customerdata.entity.UsersDataBean;

@Database(entities = {UsersDataBean.class}, version = 1, exportSchema = false)
public abstract class UserDataRoomDatabase extends RoomDatabase {

    private static final String DB_NAME = "user_database.db";

    public abstract UserDataDao userDataDao();

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile UserDataRoomDatabase INSTANCE;

    static UserDataRoomDatabase getDataBase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserDataRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), UserDataRoomDatabase.class, DB_NAME).build();
                }
            }
        }
        return INSTANCE;
    }
}
