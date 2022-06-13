package com.example.perfectnavigationassistant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.perfectnavigationassistant.Utils.DrawableUtils;
import com.example.perfectnavigationassistant.bean.Dynamic;
import com.example.perfectnavigationassistant.bean.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class AddedActivity extends AppCompatActivity {

    private ImageView imageview;
    private TextView username;
    private EditText sharedtext;
    private Button btn_added, btn_quit;
    private User userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }

        Init();
//        发布按钮
        btn_added.setOnClickListener(new AddedButtonListener());
//      返回按钮
        btn_quit.setOnClickListener(new QuitButtonListener());

    }

    public void Init() {
        imageview = (ImageView) findViewById(R.id.imageview);
        username = (TextView) findViewById(R.id.username);
        sharedtext = (EditText) findViewById(R.id.sharedtext);
        btn_added = (Button) findViewById(R.id.added);
        btn_quit = (Button) findViewById(R.id.quit);

        Intent intent = getIntent();
        String Username = intent.getStringExtra("username");
        byte[] myAvatars = intent.getByteArrayExtra("myAvatar");
        Bitmap myAvatar = BitmapFactory.decodeByteArray(myAvatars, 0, myAvatars.length);
        Bitmap roundMyAvatar = DrawableUtils.SetRoundCornerBitmap(myAvatar, 15);

        username.setText(Username);
        imageview.setImageBitmap(roundMyAvatar);

////        从数据库中获取头像
        BmobQuery<User> userBmobQuery = new BmobQuery<User>();
        userBmobQuery.addWhereEqualTo("username", Username);

//        Toast.makeText(this, "userName:" + Username, Toast.LENGTH_SHORT).show();

        userBmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
//                Toast.makeText(AddedActivity.this, "LIST：" + list.size(), Toast.LENGTH_SHORT).show();
                if (e == null) {
                    for (User userItem : list) {
//                      从数据库中获取头像
                        String Userimg = userItem.getUserImg();
                        userInfo = userItem;
//                        imageview.setImageResource(R.drawable.img_1);
                    }
                }
            }
        });
    }

    //发布按钮
    class AddedButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String Sharedtext = sharedtext.getText().toString();

            if (TextUtils.isEmpty(Sharedtext)) {
                Toast.makeText(AddedActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                return;
            } else {
//                  保存数据到数据库
                Dynamic dynamic = new Dynamic();
//                    UserName.setUsername(Username);
                if (null != userInfo) {
                    dynamic.setUserId(userInfo);
                    dynamic.setDynamicContent(Sharedtext);
                    dynamic.setLove(0);
//                    Toast.makeText(AddedActivity.this, "准备发布", Toast.LENGTH_SHORT).show();
                    dynamic.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
//                              发布成功，获取发布时间
                                String Publishtime = dynamic.getCreatedAt();
                                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = null;
                                try {
                                    date = (Date) formatter.parse(Publishtime);
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                                BmobDate PublishTime = new BmobDate(date);
                                dynamic.setPublishTime(PublishTime);
                                Toast.makeText(AddedActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddedActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Intent intent = new Intent(AddedActivity.this, SharedActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AddedActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //返回按钮
    class QuitButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
//            String Username = username.getText().toString();
//            Intent intent = new Intent(AddedActivity.this, SharedActivity.class);
//            intent.putExtra("username", Username);
//            startActivity(intent);
            finish();
        }
    }
}
