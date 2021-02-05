package ichir0roie.mine.monthlyremainmoney.MyDB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import ichir0roie.mine.monthlyremainmoney.R;

public class AAdptSBSC extends ArrayAdapter<SBSC> {
    public AAdptSBSC(@NonNull Context context, List<SBSC> resource) {
        super(context, 0,resource);
    }

    @Override
    public View getView(int pos, View cvtV, ViewGroup parent){
        SBSC data=getItem(pos);
        if(cvtV==null){
            cvtV = LayoutInflater.from(getContext()).inflate(R.layout.fxd_lv_item, parent, false);
        }
        TextView name=(TextView)cvtV.findViewById(R.id.fxd_tv_name);
        TextView value=(TextView)cvtV.findViewById(R.id.fxd_tv_value);
        name.setText(data.name);
        value.setText(String.valueOf(data.value));

        return cvtV;
    }
}
