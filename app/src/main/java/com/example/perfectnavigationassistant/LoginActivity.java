package com.example.perfectnavigationassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.perfectnavigationassistant.Utils.DrawableUtils;
import com.example.perfectnavigationassistant.bean.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button btn_login, btn_register;

    Bitmap roundAvatarImg = null;
    SharedPreferences preferences = null;
    SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }

        Init();
    }

    public void Init() {
        Bmob.initialize(this, "be29b9fb7187aa9f260f86cc427473ed");

        //从本地读取状态
//        preferences = getPreferences(MODE_PRIVATE);
        preferences = getSharedPreferences("localAutoData", MODE_PRIVATE);
        editor = preferences.edit();

        //======================报错点
        boolean loginStatus = preferences.getBoolean("loginStatus", false);
//        System.out.println("读取本地：" + loginStatus);
//        Toast.makeText(this, "loginStatus:" + loginStatus, Toast.LENGTH_SHORT).show();
        //本地处于登录状态
        if (loginStatus) {
//            String localPassWord = preferences.getString("passWord", "");
            String localUserName = preferences.getString("userName", "");
            //从数据库中获取用户名，密码判断是否正确
            BmobQuery<User> userBmobQuery = new BmobQuery<User>();
            userBmobQuery.addWhereEqualTo("username", localUserName);
            userBmobQuery.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if (e == null) {
                        for (User userItem : list) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", localUserName);

                            //====
                            //拉取头像
                            byte[] avatarByte = null;
                            try {
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            URL avatarUrl = new URL(userItem.getUserImg());
                                            URLConnection connection = avatarUrl.openConnection();
                                            connection.setConnectTimeout(0);
                                            connection.setDoInput(true);
                                            connection.connect();

                                            InputStream fis = connection.getInputStream();
                                            Bitmap avatarImg = BitmapFactory.decodeStream(fis);
                                            fis.close();
                                            roundAvatarImg = DrawableUtils.SetRoundCornerBitmap(avatarImg, 15);
                                        } catch (IOException ioException) {
                                            ioException.printStackTrace();
                                        }
                                    }
                                });
                                thread.start();
                                thread.join();
//                                    Message message = new Message();
//                                    message.obj = roundAvatarImg;

                                //实例化字节数组输出流
                                ByteArrayOutputStream avatarStream = new ByteArrayOutputStream();
                                //压缩位图
                                roundAvatarImg.compress(Bitmap.CompressFormat.PNG, 0, avatarStream);
//                                Toast.makeText(LoginActivity.this, "avatarStream:" + avatarStream.size(), Toast.LENGTH_SHORT).show();
                                //创建分配字节数组
                                avatarByte = avatarStream.toByteArray();

                            } catch (Exception interruptedException) {
                                interruptedException.printStackTrace();
                            }
                            intent.putExtra("myAvatar", avatarByte);

                            //====
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            });
        } else {
            btn_login = (Button) findViewById(R.id.btn_login);
            btn_register = (Button) findViewById(R.id.btn_register);
            username = (EditText) findViewById(R.id.username);
            password = (EditText) findViewById(R.id.password);

            //本地处于未登录状态，需要给按钮添加监听事件
            //登录
            btn_login.setOnClickListener(new LoginButtonListener());
            //注册
            btn_register.setOnClickListener(new RegisterButtonListener());

            //接收数据
            Intent intent = getIntent();
            String Username = intent.getStringExtra("username");
            username.setText(Username);
            String Password = intent.getStringExtra("password");
            password.setText(Password);
        }
    }

    //登录按钮
    class LoginButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String Username = username.getText().toString();
            String Password = password.getText().toString();
            //从数据库中获取用户名，密码判断是否正确
            BmobQuery<User> userBmobQuery = new BmobQuery<User>();
            userBmobQuery.addWhereEqualTo("username", Username);
            userBmobQuery.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if (e == null) {
                        for (User userItem : list) {
                            String PassWord = userItem.getPassword();
                            if (PassWord.equals(Password))//判断密码
                            {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("username", Username);

                                //====
                                //拉取头像
                                byte[] avatarByte = null;
                                try {
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                URL avatarUrl = new URL(userItem.getUserImg());
                                                URLConnection connection = avatarUrl.openConnection();
                                                connection.setConnectTimeout(0);
                                                connection.setDoInput(true);
                                                connection.connect();

                                                InputStream fis = connection.getInputStream();
                                                Bitmap avatarImg = BitmapFactory.decodeStream(fis);
                                                fis.close();
                                                roundAvatarImg = DrawableUtils.SetRoundCornerBitmap(avatarImg, 15);
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        }
                                    });
                                    thread.start();
                                    thread.join();
//                                    Message message = new Message();
//                                    message.obj = roundAvatarImg;

                                    //实例化字节数组输出流
                                    ByteArrayOutputStream avatarStream = new ByteArrayOutputStream();
                                    //压缩位图
                                    roundAvatarImg.compress(Bitmap.CompressFormat.PNG, 0, avatarStream);
//                                    Toast.makeText(LoginActivity.this, "avatarStream:" + avatarStream.size(), Toast.LENGTH_SHORT).show();
                                    //创建分配字节数组
                                    avatarByte = avatarStream.toByteArray();

                                } catch (Exception interruptedException) {
                                    interruptedException.printStackTrace();
                                }

                                intent.putExtra("myAvatar", avatarByte);
                                //====
                                //登录成功，将登录状态、用户名 存储到本地
                                editor.putString("userName", Username);
                                editor.putBoolean("loginStatus", true);
                                editor.apply();

                                Boolean loginStatus = preferences.getBoolean("loginStatus", true);
                                System.out.println(loginStatus);
//                                Toast.makeText(LoginActivity.this, "loginStatus:" + loginStatus, Toast.LENGTH_SHORT).show();

                                startActivity(intent);
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "输入的账号或密码不正确", Toast.LENGTH_LONG).show();
//                                username.setText("");
                                password.setText("");
                            }
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    //注册按钮
    class RegisterButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }

}