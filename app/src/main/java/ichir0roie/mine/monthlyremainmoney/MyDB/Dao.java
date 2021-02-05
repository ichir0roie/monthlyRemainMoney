package ichir0roie.mine.monthlyremainmoney.MyDB;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@androidx.room.Dao
public interface Dao {

    @Query("select * from SBSC")
    List<SBSC> getAll();

    @Query("SELECT * FROM SBSC WHERE id IN (:ids)")
    List<SBSC> loadAllByIds(int[] ids);

    @Insert
    void insertAll(SBSC data);

    @Delete
    void delete(SBSC column);

    @Update(entity = SBSC.class)
    void update(SBSC column);

}
