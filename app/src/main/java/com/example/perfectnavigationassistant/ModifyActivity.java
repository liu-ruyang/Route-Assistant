package com.example.perfectnavigationassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.perfectnavigationassistant.bean.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ModifyActivity extends AppCompatActivity {

    private EditText username, birthday, password;
    private Button btn_quit, btn_submit;
    private RadioGroup sex;
    private RadioButton btn;
    private String Username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }

//        Toast.makeText(this, "跳转到了修改个人信息界面", Toast.LENGTH_SHORT).show();
        Init();
        sex.setOnCheckedChangeListener(new SexButtonListener());
        //确认按钮跳转
        btn_submit.setOnClickListener(new SubmitButtonListener());
        //返回按钮跳转
        btn_quit.setOnClickListener(new QuitButtonListener());

    }

    public void Init() {
        username = (EditText) findViewById(R.id.username);
        birthday = (EditText) findViewById(R.id.birthday);
        password = (EditText) findViewById(R.id.password);
        btn_quit = (Button) findViewById(R.id.quit);
        btn_submit = (Button) findViewById(R.id.submit);
        sex = (RadioGroup) findViewById(R.id.sex);

        Intent intent = getIntent();
        Username = intent.getStringExtra("username");
        username.setText(Username);
        String Birthday = intent.getStringExtra("birthday");
        birthday.setText(Birthday);

        //从数据库中获取密码
        BmobQuery<User> userBmobQuery = new BmobQuery<User>();
        userBmobQuery.addWhereEqualTo("username", Username);
        userBmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    for (User userItem : list) {
                        String Password = userItem.getPassword();
                        password.setText(Password);
                    }
                }
            }
        });
    }

    //    性别选择
    class SexButtonListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

        }
    }

    //    确认按钮
    class SubmitButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String UserName = username.getText().toString();
            String Birthday = birthday.getText().toString();
            String Password = password.getText().toString();
            btn = (RadioButton) findViewById(sex.getCheckedRadioButtonId());
            String Gender = btn.getText().toString();
//          将数据保存到数据库中
            BmobQuery<User> userBmobQuery = new BmobQuery<User>();
            userBmobQuery.addWhereEqualTo("username", Username);
            userBmobQuery.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if (e == null) {
                        for (User userItem : list) {
                            userItem.setUsername(UserName);
                            userItem.setPassword(Password);
                            userItem.setSex(Gender);
//                              将String型转化为BmobDate型，保存出生日期到数据库
                            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Date Birth = null;
                            try {
                                Birth = formatter.parse(Birthday);
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                            BmobDate BirthDay = new BmobDate(Birth);
                            userItem.setBirth(BirthDay);

                            String Userid = userItem.getObjectId();

                            userItem.update(Userid, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    //结果回调
                                    if (e == null) {
                                        //先更新本地存储的个人信息（用于登录）
                                        SharedPreferences preferences = getSharedPreferences("localAutoData", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("userName", UserName);
                                        editor.apply();

                                        Toast.makeText(ModifyActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                        //使用数据回传的方式，将修改的数据回传至上一个Activity，以修改上一个Activity的界面
                                        Intent intent = new Intent();
//                                        intent.putExtra("myAvatar", getIntent().getByteArrayExtra("myAvatar"));
                                        intent.putExtra("username", UserName);
//                                        startActivity(intent);
//                                        Toast.makeText(ModifyActivity.this, "修改后的数据是：" + intent.getStringExtra("username"), Toast.LENGTH_SHORT).show();
                                        setResult(1, intent);
                                        finish();
                                    } else {
                                        Toast.makeText(ModifyActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            });


        }
    }

    //    返回按钮
    class QuitButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
//            Intent intent = new Intent(ModifyActivity.this, InformationActivity.class);
//            startActivity(intent);
            finish();
        }
    }

}
