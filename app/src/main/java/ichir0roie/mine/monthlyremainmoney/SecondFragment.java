package ichir0roie.mine.monthlyremainmoney;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class SecondFragment extends Fragment {

    Button bt_balance;
    Button bt_set;
    Button bt_rate;
    EditText et_balance;
    EditText et_set;
    EditText et_rate;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ListView lv_fixed;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        sharedPreferences=view.getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        et_balance=view.findViewById(R.id.et_set_balance);
        et_set=view.findViewById(R.id.et_set_set);
        et_rate=view.findViewById(R.id.et_set_rate);

        et_balance.setText(String.valueOf(sharedPreferences.getInt("balance",0)));
        et_rate.setText(String.valueOf(sharedPreferences.getInt("rate",0)));
        et_set.setText(String.valueOf(sharedPreferences.getInt("set",0)));

        lv_fixed=view.findViewById(R.id.setup_lv_fixed);


        view.findViewById(R.id.bt_add_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        view.findViewById(R.id.bt_set_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor.putInt("balance",Integer.parseInt(et_balance.getText().toString()));
                editor.putInt("set",Integer.parseInt(et_set.getText().toString()));
                editor.putInt("rate",Integer.parseInt(et_rate.getText().toString()));
                editor.apply();


                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }


}
