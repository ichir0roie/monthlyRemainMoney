package ichir0roie.mine.monthlyremainmoney.MyDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SBSC.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract Dao dao();
}
