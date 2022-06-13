package com.example.perfectnavigationassistant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.perfectnavigationassistant.Utils.DrawableUtils;
import com.example.perfectnavigationassistant.bean.User;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class InformationActivity extends AppCompatActivity {

    private TextView username, sex, balance, birthday, vip;
    private ImageView imageview;
    private Button btn_quit, btn_modify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }

        Init();
//        修改个人信息按钮跳转
        btn_modify.setOnClickListener(new ModifyButtonListener());
//        返回我的按钮跳转
        btn_quit.setOnClickListener(new QuitButtonListener());

    }

    public void Init() {
        Bmob.initialize(this, "be29b9fb7187aa9f260f86cc427473ed");
        imageview = (ImageView) findViewById(R.id.imageview);
        username = (TextView) findViewById(R.id.username);
        sex = (TextView) findViewById(R.id.sex);
        balance = (TextView) findViewById(R.id.balance);
        birthday = (TextView) findViewById(R.id.birthday);
        vip = (TextView) findViewById(R.id.vip);
        btn_quit = (Button) findViewById(R.id.quit);
        btn_modify = (Button) findViewById(R.id.modify);

//        接收数据
        Intent intent = getIntent();
        String Username = intent.getStringExtra("username");
        byte[] myAvatars = intent.getByteArrayExtra("myAvatar");
        Bitmap myAvatar = BitmapFactory.decodeByteArray(myAvatars, 0, myAvatars.length);
        Bitmap roundMyAvatar = DrawableUtils.SetRoundCornerBitmap(myAvatar, 15);

        username.setText(Username);
        imageview.setImageBitmap(roundMyAvatar);


//        从数据库中获取头像，性别，余额，出生日期，是否会员信息
        BmobQuery<User> userBmobQuery = new BmobQuery<User>();
        userBmobQuery.addWhereEqualTo("username", Username);
        userBmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    for (User userItem : list) {
                        String Sex = userItem.getSex();
                        sex.setText(Sex);

                        String Birthday = userItem.getBirth().getDate();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = null;
                        try {
                            date = sdf.parse(Birthday);
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }
                        birthday.setText(date.getYear() + 1900 + "-" + (date.getMonth() + 1) + "-" + date.getDate());

                        Double BALANCE = userItem.getBalance();
                        DecimalFormat format = new DecimalFormat("#0.00");
                        String Balance = format.format(BALANCE);
                        balance.setText(Balance);

                        Integer Vip = userItem.getVip();
                        if (Vip.equals(1)) {
                            vip.setText("是");
                        } else {
                            vip.setText("否");
                        }

//                      从数据库中获取头像
                        //String Userimg = userItem.getUserImg();
                    }
                }
            }
        });
    }

    //    修改个人信息按钮
    class ModifyButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String Username = username.getText().toString();
            String Birthday = birthday.getText().toString();
//            System.out.println("跳转向修改个人页面");
            Intent intent = new Intent(InformationActivity.this, ModifyActivity.class);
            intent.putExtra("username", Username);
            intent.putExtra("birthday", Birthday);
//            intent.putExtra("myAvatar", getIntent().getByteArrayExtra("myAvatar"));
//            startActivity(intent);
            startActivityForResult(intent, 1);
        }
    }

    //修改完数据，接收修改后的数据，用新的数据更新界面
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == 1) {
                /*获取另一个Activity传回来的数据*/
                String username1 = data.getStringExtra("username");

                username.setText(username1);

//        从数据库中获取头像，性别，余额，出生日期，是否会员信息
                BmobQuery<User> userBmobQuery = new BmobQuery<User>();
                userBmobQuery.addWhereEqualTo("username", username1);
                userBmobQuery.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null) {
                            for (User userItem : list) {
                                String Sex = userItem.getSex();
                                sex.setText(Sex);

                                String Birthday = userItem.getBirth().getDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = null;
                                try {
                                    date = sdf.parse(Birthday);
                                } catch (ParseException parseException) {
                                    parseException.printStackTrace();
                                }
                                birthday.setText(date.getYear() + 1900 + "-" + (date.getMonth() + 1) + "-" + date.getDate());

                                Double BALANCE = userItem.getBalance();
                                DecimalFormat format = new DecimalFormat("#0.##");
                                String Balance = format.format(BALANCE);
                                balance.setText(Balance);

                                Integer Vip = userItem.getVip();
                                if (Vip.equals(1)) {
                                    vip.setText("是");
                                } else {
                                    vip.setText("否");
                                }

//                      从数据库中获取头像
                                String Userimg = userItem.getUserImg();

                                Intent intent = getIntent();
                                byte[] myAvatars = intent.getByteArrayExtra("myAvatar");
                                Bitmap myAvatar = BitmapFactory.decodeByteArray(myAvatars, 0, myAvatars.length);
                                Bitmap roundMyAvatar = DrawableUtils.SetRoundCornerBitmap(myAvatar, 15);
                                imageview.setImageBitmap(roundMyAvatar);
                            }
                        }
                    }
                });
            }
        }
    }

    //    返回按钮
    class QuitButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String Username = username.getText().toString();
//            Intent intent = new Intent(InformationActivity.this, MineActivity.class);
//            intent.putExtra("username", Username);
//            startActivity(intent);
            Intent intent = new Intent();
            intent.putExtra("username", Username);
            intent.putExtra("from", "information");
            setResult(1, intent);
            finish();
        }
    }

}
