package com.example.perfectnavigationassistant;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.perfectnavigationassistant.bean.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, password, birthday;
    private Button btn_quit, btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }

        Init();
//        注册按钮
        btn_register.setOnClickListener(new RegisterButtonListener());
//        返回按钮
        btn_quit.setOnClickListener(new QuitButtonListener());
    }

    public void Init() {
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_quit = (Button) findViewById(R.id.btn_quit);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        birthday = (EditText) findViewById(R.id.birthday);
    }

    //    注册按钮
    class RegisterButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String Username = username.getText().toString().trim();
            String Password = password.getText().toString().trim();
            String Birthday = birthday.getText().toString().trim();

            if (TextUtils.isEmpty(Username)) {
                Toast.makeText(RegisterActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(Password)) {
                Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(Birthday)) {
                Toast.makeText(RegisterActivity.this, "请出生日期", Toast.LENGTH_SHORT).show();
                return;
            } else if (!dateStrIsValid(Birthday, "yyyy-MM-dd")) {
                //判断输入日期格式
                Toast.makeText(RegisterActivity.this, "日期格式错误", Toast.LENGTH_SHORT).show();
                birthday.setText("");
                return;
            } else {
                BmobQuery<User> userBmobQuery = new BmobQuery<User>();
                userBmobQuery.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null) {
                            int count = 0;
                            for (User userItem : list) {
                                if (userItem.getUsername().equals(Username)) {
                                    Toast.makeText(RegisterActivity.this, "此用户名已经存在", Toast.LENGTH_SHORT).show();
                                    username.setText("");
                                    password.setText("");
                                    birthday.setText("");
                                    break;
                                }
                                count++;
                            }
//                              查询到尾，说明没有重复账号
                            if (count == list.size()) {
//                                  保存数据到数据库
                                User newUser = new User();
                                newUser.setUsername(Username);
                                newUser.setPassword(Password);
                                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                Date Birth = null;
                                try {
                                    Birth = formatter.parse(Birthday);
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                                BmobDate BirthDay = new BmobDate(Birth);
                                newUser.setBirth(BirthDay);

                                newUser.setBalance(0.0);
                                newUser.setVip(0);
                                newUser.setUserImg("https://mp2.iqiyipic.com/image/20210601/8c/e6/ppu_1622505612720691_pp_601_300_300.jpg");
                                //注册用户
                                newUser.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            //注册成功，回到登录页面
                                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            intent.putExtra("username", Username);
                                            intent.putExtra("password", Password);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 验证字符串是否为指定日期格式
     *
     * @param oriDateStr 待验证字符串
     * @param pattern    日期字符串格式, 例如 "yyyy-MM-dd"
     * @return 有效性结果, true 为正确, false 为错误
     */
    public static boolean dateStrIsValid(String oriDateStr, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            Date date = dateFormat.parse(oriDateStr);
            return oriDateStr.equals(dateFormat.format(date));
        } catch (ParseException e) {
            return false;
        }
    }


    //    返回按钮跳转
    class QuitButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
//            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//            startActivity(intent);
            finish();
        }
    }

}
