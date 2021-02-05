package ichir0roie.mine.monthlyremainmoney;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.Executor;

import ichir0roie.mine.monthlyremainmoney.MyDB.AAdptSBSC;
import ichir0roie.mine.monthlyremainmoney.MyDB.AppDatabase;
import ichir0roie.mine.monthlyremainmoney.MyDB.Dao;
import ichir0roie.mine.monthlyremainmoney.MyDB.SBSC;

public class SecondFragment extends Fragment {

    Button bt_balance;
    Button bt_set;
    Button bt_rate;
    EditText et_balance;
    EditText et_set;
    EditText et_rate;

    EditText et_name;
    EditText et_value;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ListView lv_fixed;

    List<SBSC> LvDt;

    Executor executor;
    LvDtGet lvGt;
    LvDtIns lvIs;
    LvDtDel lvDl;
    LvDtUpd lvUd;

    Context GContext;
    View GView;

    Handler GHandler;


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

        GContext=this.requireContext();
        GView=this.getView();
        GHandler=new Handler();

        executor= new myExecutor();

        sharedPreferences=view.getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        et_balance=view.findViewById(R.id.et_set_balance);
        et_set=view.findViewById(R.id.et_set_set);
        et_rate=view.findViewById(R.id.et_set_rate);

        et_name=view.findViewById(R.id.et_ins_name);
        et_value=view.findViewById(R.id.et_ins_val);

        et_balance.setText(String.valueOf(sharedPreferences.getInt("balance",0)));
        et_rate.setText(String.valueOf(sharedPreferences.getInt("rate",0)));
        et_set.setText(String.valueOf(sharedPreferences.getInt("set",0)));

        lv_fixed=view.findViewById(R.id.setup_lv_fixed);

        lvSetup();

        view.findViewById(R.id.bt_add_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvIs=new LvDtIns();
                lvIs.setClm(
                        GContext,
                        et_name.getText().toString(),
                        et_value.getText().toString());
                executor.execute(lvIs);

                lvSetup();

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

    Runnable lvSetter=new Runnable() {
        @Override
        public void run() {
            AAdptSBSC adaptor=new AAdptSBSC(GContext,lvGt.data);
            lv_fixed.setAdapter(adaptor);
            lv_fixed.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    lvDl = new LvDtDel();
                    lvDl.setTargetAndContext(GContext, lvGt.data.get(position).id);
                    executor.execute(lvDl);

                    lvSetup();

                    return false;
                }
            });

            lv_fixed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    lvUd = new LvDtUpd();
                    SBSC revBought = lvGt.data.get(position);
                    revBought.buy = !revBought.buy;
                    lvUd.setTargetAndContext(GContext, revBought);
                    executor.execute(lvUd);

                    lvSetup();
                }
            });

            //todo add update bought flag when click
        }
    };

    public void lvSetup(){

        lvGt=new LvDtGet();
        lvGt.setContextAndView(GContext,GView,lvSetter,GHandler);
        executor.execute(lvGt);

    }

    static class LvDtGet implements Runnable {
        Context appCtt;
        List<SBSC> data;
        View view;
        Runnable setLv;
        Handler mainHdl;

        @Override
        public void run() {
            AppDatabase db = Room.databaseBuilder(appCtt,
                    AppDatabase.class, "database-name").build();
            Dao dao= db.dao();
            List<SBSC> list=dao.getAll();
            data=list;

            mainHdl.post(setLv);

        }
        public void setContextAndView(Context context,View view,Runnable setLv,Handler mainHdl){
            this.appCtt=context;
            this.view=view;
            this.setLv=setLv;
            this.mainHdl=mainHdl;
        }
    }

    static class LvDtIns implements Runnable{
        Context appCtt;
        SBSC clm;
        @Override
        public void run() {
            AppDatabase db = Room.databaseBuilder(appCtt,
                    AppDatabase.class, "database-name").build();
            Dao dao= db.dao();
            dao.insertAll(clm);
        }
        public void setClm(Context context,String name,String value){
            appCtt=context;
            clm=new SBSC();
            clm.name=name;
            clm.buy=false;
            int valueIns=0;
            if(!value.equals("")){
                valueIns=Integer.parseInt(value);
            }
            clm.value=valueIns;
        }
    }

    static class LvDtDel implements Runnable{
        Context context;
        int target;

        @Override
        public void run() {
            AppDatabase db = Room.databaseBuilder(context,
                    AppDatabase.class, "database-name").build();
            Dao dao = db.dao();
            SBSC tO = new SBSC();
            tO.id = target;
            dao.delete(tO);
        }

        public void setTargetAndContext(Context context, int target) {
            this.context = context;
            this.target = target;
        }
    }

    static class myExecutor implements Executor {
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }

    static class LvDtUpd implements Runnable {
        Context context;
        SBSC target;

        @Override
        public void run() {
            AppDatabase db = Room.databaseBuilder(context,
                    AppDatabase.class, "database-name").build();
            Dao dao = db.dao();
            dao.update(target);

        }

        public void setTargetAndContext(Context context, SBSC target) {
            this.context = context;
            this.target = target;
        }
    }

}
