package com.example.perfectnavigationassistant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.perfectnavigationassistant.Utils.DrawableUtils;
import com.example.perfectnavigationassistant.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class VIPActivity extends AppCompatActivity {

    private ImageView img_vip_avatar = null;
    private TextView tx_vip_username = null;
    private TextView tx_vip_level = null;

    @Override

    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.payment);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }

        bindViews();
        init();
    }

    //绑定控件
    public void bindViews() {
        img_vip_avatar = findViewById(R.id.img_vip_avatar);
        tx_vip_username = findViewById(R.id.tx_vip_username);
        tx_vip_level = findViewById(R.id.tx_vip_level);
    }

    //初始化界面
    public void init() {
        Intent intent = getIntent();
        String Username = intent.getStringExtra("username");
        byte[] myAvatars = intent.getByteArrayExtra("myAvatar");
        Bitmap myAvatar = BitmapFactory.decodeByteArray(myAvatars, 0, myAvatars.length);
        Bitmap roundMyAvatar = DrawableUtils.SetRoundCornerBitmap(myAvatar, 15);

        tx_vip_username.setText(Username);
        img_vip_avatar.setImageBitmap(roundMyAvatar);


        BmobQuery<User> userBmobQuery = new BmobQuery<User>();
        userBmobQuery.addWhereEqualTo("username", Username);
        userBmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    for (User userItem : list) {
                        Integer Vip = userItem.getVip();
                        if (Vip.equals(1)) {
                            tx_vip_level.setText("尊享会员");
                        } else {
                            tx_vip_level.setText("普通用户");
                        }
                    }
                }
            }
        });
    }
}
