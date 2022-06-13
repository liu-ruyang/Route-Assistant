package com.example.perfectnavigationassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.perfectnavigationassistant.bean.User;

import java.text.DecimalFormat;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class BalanceActivity extends AppCompatActivity {

    private TextView username;
    private TextView balance;
    private Button btn_quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }

        Init();
//       返回按钮跳转
        btn_quit.setOnClickListener(new QuitButtonListener());

    }

    public void Init() {
        //username = (TextView) findViewById(R.id.username);
        btn_quit = (Button) findViewById(R.id.quit);
        balance = (TextView) findViewById(R.id.balance);

//        接收数据
        Intent intent = getIntent();
        String Username = intent.getStringExtra("username");
        //username.setText(Username);

//      从数据库中获取余额信息
        BmobQuery<User> userBmobQuery = new BmobQuery<User>();
        userBmobQuery.addWhereEqualTo("username", Username);
        userBmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    for (User userItem : list) {
                        Double BALANCE = userItem.getBalance();
                        DecimalFormat format = new DecimalFormat("#0.##");
                        String Balance = format.format(BALANCE);
                        balance.setText(Balance);
                    }
                }
            }
        });
    }

    //       返回按钮跳转
    class QuitButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String Username = username.getText().toString();
//            Intent intent = new Intent(BalanceActivity.this, MineActivity.class);
//            intent.putExtra("username", Username);
//            startActivity(intent);
            Intent intent = new Intent();
            intent.putExtra("from", "balance");
            setResult(1, intent);
            finish();
        }
    }
}
