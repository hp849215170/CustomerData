package m.hp.customerdata.room;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import m.hp.customerdata.dao.UserDataDao;
import m.hp.customerdata.entity.MessageBean;

/**
 * Database migrations are beyond the scope of this codelab, so we set exportSchema to false here to avoid a build warning
 */
@Database(entities = {MessageBean.class}, version = 1, exportSchema = false)
public abstract class UserDataRoomDatabase extends RoomDatabase {

    private static final String TAG = "UserDataRoomDatabase";

    private static final String DB_NAME = "user_database.db";

    public abstract UserDataDao userDataDao();

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile UserDataRoomDatabase INSTANCE;

    static UserDataRoomDatabase getDataBase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserDataRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), UserDataRoomDatabase.class, DB_NAME).addCallback(callback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                Log.d(TAG, "重新安装了应用");
            });
        }
    };
}
