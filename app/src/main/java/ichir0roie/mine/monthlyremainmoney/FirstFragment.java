package ichir0roie.mine.monthlyremainmoney;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;

import ichir0roie.mine.monthlyremainmoney.MyDB.AppDatabase;
import ichir0roie.mine.monthlyremainmoney.MyDB.Dao;
import ichir0roie.mine.monthlyremainmoney.MyDB.SBSC;

public class FirstFragment extends Fragment {

    //TextView tv_fee_lunch;
    TextView tv_fee_dinner;
    TextView tv_fee_pocket;

    TextView tv_lbl_pay;

    EditText et_balance;

    SharedPreferences shared;

    TextView tv_bal_tar;

    boolean flgTomorrow = false;

    OCR ocr;

    Handler mainHd;
    Executor executor;

    getToMonthFixed rblGetFixed;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    //output data
    //int fee_lunch=0;
    int todayConsum = 0;

    private void saveBalance() {
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt("balance", balance);
        editor.apply();
        editor.clear();
    }

    final private int READ_REQUEST_CODE = 1;
    Bitmap ResultBitmap = null;

    public void ocrAction() {
//        if (ResultBitmap==null){
//            ocr=new OCR(getContext());
//            Intent intent=new Intent( Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("image/*");
//            startActivityForResult(intent,READ_REQUEST_CODE);
//            Toast.makeText(requireContext(),"setup",Toast.LENGTH_SHORT).show();
//        }else{
//            String resultText=ocr.getText(requireContext(),ResultBitmap);
//            ResultBitmap=null;
//            Toast.makeText(requireContext(),"get text",Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ResultBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateWidget() {
        Intent widgetUpdate = new Intent();
        widgetUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

    }

    Runnable setValues = new Runnable() {
        @Override
        public void run() {
            calcOutput();
            setTexts();
        }
    };
    int pocketMoney = 0;

    //load data
    int targetBalance = 0;
    int balance = 0;
    int todayLimit = 0;
    int tgtSav = 0;

    private void addBalance(int add) {
        balance += add;
        et_balance.setText(String.valueOf(balance));
        calcUpdate();
        saveBalance();
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainHd = new Handler(Looper.getMainLooper());
        executor = new myExecutor();

        //tv_fee_lunch=(TextView)view.findViewById(R.id.main_tb_lunchMoney);
        tv_fee_dinner = (TextView) view.findViewById(R.id.main_tb_dinnerMonery);
        tv_fee_pocket = (TextView) view.findViewById(R.id.main_tb_pocketMoney);
        tv_bal_tar = (TextView) view.findViewById(R.id.main_iplb_tar_save);
        tv_lbl_pay = (TextView) view.findViewById(R.id.main_lbl_pay);

        et_balance = (EditText) view.findViewById(R.id.main_et_balance);

        view.findViewById(R.id.main_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        shared=view.getContext().getSharedPreferences("data", Context.MODE_PRIVATE);

        view.findViewById(R.id.main_bt_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balance=Integer.parseInt(et_balance.getText().toString());
                calcUpdate();
                saveBalance();
            }
        });

        view.findViewById(R.id.main_bt_tod).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flgTomorrow=false;
                tv_lbl_pay.setText("今日の食費");
                calcUpdate();
            }
        });

        view.findViewById(R.id.main_bt_tom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flgTomorrow=true;
                tv_lbl_pay.setText("明日の食費");
                calcUpdate();
            }
        });
        view.findViewById(R.id.main_bt_ocr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocrAction();
            }
        });
        view.findViewById(R.id.main_bt_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBalance(100);
            }
        });
        view.findViewById(R.id.main_bt_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBalance(-100);
            }
        });
        view.findViewById(R.id.main_bt_plus2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBalance(500);
            }
        });
        view.findViewById(R.id.main_bt_minus2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBalance(-500);
            }
        });


        setData();

//        ocr=new OCR(requireContext());
    }

    private void setData() {
        targetBalance = shared.getInt("set", 0);
        balance = shared.getInt("balance", 0);
        todayLimit = shared.getInt("rate", 0);
    }

    private void calcUpdate() {
        rblGetFixed = new getToMonthFixed();
        rblGetFixed.setContextAndView(this.requireContext(), this.getView(), setValues, mainHd);
        executor.execute(rblGetFixed);
    }

    private void calcOutput() {
        Calendar now = Calendar.getInstance();
        Calendar end_month = Calendar.getInstance();
        //int day_of_month=end_month.get(Calendar.DAY_OF_MONTH);
        end_month.set(Calendar.DAY_OF_MONTH, 1);
        end_month.add(Calendar.MONTH, 1);
        end_month.add(Calendar.DAY_OF_MONTH, -1);
        int today = now.get(Calendar.DAY_OF_MONTH) - 1;
        int end = end_month.get(Calendar.DAY_OF_MONTH);
        int days_left = end - today;
        if (flgTomorrow) {
            days_left--;
        }

        int fixed = 0;
        for (int i = 0; i < rblGetFixed.data.size(); i++) {
            SBSC d = rblGetFixed.data.get(i);
            if (!d.buy) {
                fixed += d.value;
            }
        }

        int remain = balance - fixed;
        todayConsum = (remain / days_left/100)*100;
        if (todayConsum > todayLimit) {
            todayConsum = todayLimit;
        }
        tgtSav = todayLimit * days_left + fixed - todayConsum;
        pocketMoney = balance - todayConsum;


    }

    @Override
    public void onResume() {
        super.onResume();
        calcUpdate();
    }

    private void setTexts() {
        //tv_fee_lunch.setText(String.valueOf(fee_lunch));
        tv_fee_dinner.setText(String.valueOf((int) todayConsum));
        tv_fee_pocket.setText(String.valueOf((int) pocketMoney));
        tv_bal_tar.setText(String.valueOf((int) tgtSav));
        et_balance.setText(String.valueOf((int) balance));
    }

    static class getToMonthFixed implements Runnable {
        Context appCtt;
        List<SBSC> data;
        View view;
        Runnable setVals;
        Handler mainHdl;

        @Override
        public void run() {
            AppDatabase db = Room.databaseBuilder(appCtt,
                    AppDatabase.class, "database-name").build();
            Dao dao = db.dao();
            data = dao.getAll();

            mainHdl.post(setVals);

        }

        public void setContextAndView(Context context, View view, Runnable setLv, Handler mainHdl) {
            this.appCtt = context;
            this.view = view;
            this.setVals = setLv;
            this.mainHdl = mainHdl;
        }
    }

    static class myExecutor implements Executor {
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }


}
