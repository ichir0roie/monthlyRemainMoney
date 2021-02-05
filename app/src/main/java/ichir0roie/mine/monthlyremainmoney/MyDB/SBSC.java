package ichir0roie.mine.monthlyremainmoney.MyDB;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

@androidx.room.Entity
public class SBSC {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="name")
    public String name;

    @ColumnInfo(name = "value")
    public int value;

    @ColumnInfo(name="buy")
    public boolean buy;
}


