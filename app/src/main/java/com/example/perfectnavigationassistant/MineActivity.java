package com.example.perfectnavigationassistant;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.perfectnavigationassistant.Utils.DrawableUtils;
import com.example.perfectnavigationassistant.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

//public class MineActivity<string> extends AppCompatActivity {
public class MineActivity {
    View viewItem = null;
    Context context = null;
    //================================
    private Button btn_vip;
    private Button btn_balance, btn_places, btn_information;
    private Button btn_quit;
    private TextView username, sex;
    private ImageView imageview;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mine);
//
//        Init();
////        VIP跳转
//        btn_vip.setOnClickListener(new VipButtonListener());
////        余额跳转
//        btn_balance.setOnClickListener(new BalanceButtonListener());
////        去过的地方跳转
//        btn_places.setOnClickListener(new PlacesButtonListener());
////        个人信息跳转
//        btn_information.setOnClickListener(new InformationButtonListener());
////        退出登录跳转
//        btn_quit.setOnClickListener(new QuitButtonListener());
//    }

    public void start(Context context, View parentView) {
        this.context = context;
        viewItem = parentView;

        Init();
        //VIP跳转
        btn_vip.setOnClickListener(new VipButtonListener());
        //余额跳转
        btn_balance.setOnClickListener(new BalanceButtonListener());
        //去过的地方跳转
        btn_places.setOnClickListener(new PlacesButtonListener());
        //个人信息跳转
        btn_information.setOnClickListener(new InformationButtonListener());
        //退出登录跳转
        btn_quit.setOnClickListener(new QuitButtonListener());

    }

    public void Init() {
        btn_vip = (Button) viewItem.findViewById(R.id.vip);
        btn_balance = (Button) viewItem.findViewById(R.id.btn_balance);
        btn_places = (Button) viewItem.findViewById(R.id.btn_places);
        btn_information = (Button) viewItem.findViewById(R.id.btn_information);
        btn_quit = (Button) viewItem.findViewById(R.id.quit);
        username = (TextView) viewItem.findViewById(R.id.username);
        sex = (TextView) viewItem.findViewById(R.id.sex);
        imageview = (ImageView) viewItem.findViewById(R.id.imageview);

        //接收数据
        Intent intent = ((Activity) context).getIntent();
        String Username = intent.getStringExtra("username");
        username.setText(Username);

        //从数据库中获取头像，性别等信息
        BmobQuery<User> userBmobQuery = new BmobQuery<User>();
        userBmobQuery.addWhereEqualTo("username", Username);
        userBmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    for (User userItem : list) {
                        String Sex = userItem.getSex();
                        sex.setText(Sex);
//                      获取用户头像
//                        String Imageview = userItem.getUserImg();

                        Intent tempIntent = ((Activity) context).getIntent();
                        byte[] myAvatars = tempIntent.getByteArrayExtra("myAvatar");
                        Bitmap myAvatar = BitmapFactory.decodeByteArray(myAvatars, 0, myAvatars.length);
                        Bitmap roundMyAvatar = DrawableUtils.SetRoundCornerBitmap(myAvatar, 15);
//                        imageview.setImageResource(R.drawable.img_1);
                        imageview.setImageBitmap(roundMyAvatar);
                    }
                }
            }
        });
    }

    //vip按钮
    class VipButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent tempIntent = ((Activity) context).getIntent();
            byte[] myAvatars = tempIntent.getByteArrayExtra("myAvatar");
            String Username = username.getText().toString();


            Intent intent = new Intent(context, VIPActivity.class);
            intent.putExtra("username", Username);
            intent.putExtra("myAvatar", myAvatars);

            context.startActivity(intent);
        }
    }

    //余额按钮
    class BalanceButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String Username = username.getText().toString();
            Intent intent = new Intent(context, BalanceActivity.class);
            intent.putExtra("username", Username);
            ((Activity) context).startActivityForResult(intent, 1);
        }
    }

    //景点按钮（去过的地方）
    class PlacesButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(context, MainActivity.class);
//            context.startActivity(intent);
        }
    }

    //个人信息按钮
    class InformationButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent tempIntent = ((Activity) context).getIntent();
            byte[] myAvatars = tempIntent.getByteArrayExtra("myAvatar");
            String Username = username.getText().toString();

            Intent intent = new Intent(context, InformationActivity.class);
            intent.putExtra("username", Username);
            intent.putExtra("myAvatar", myAvatars);

//            context.startActivity(intent);
            ((Activity) context).startActivityForResult(intent, 1);
        }
    }

    //退出登录按钮
    class QuitButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String Username = username.getText().toString();

//            SharedPreferences preferences = ((Activity) context).getPreferences(Context.MODE_PRIVATE);
            SharedPreferences preferences = context.getSharedPreferences("localAutoData", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

//            Boolean loginStatus = preferences.getBoolean("loginStatus", true);
//            System.out.println("退出了：" + loginStatus);
//            Toast.makeText(context, "loginStatus:" + loginStatus, Toast.LENGTH_SHORT).show();

            editor.putString("userName", "");
            editor.putBoolean("loginStatus", false);
            editor.apply();


            Boolean loginStatus = preferences.getBoolean("loginStatus", true);
//            System.out.println("退出了：" + loginStatus);
//            Toast.makeText(context, "loginStatus:" + loginStatus, Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("username", Username);
            Toast.makeText(context, "退出登录", Toast.LENGTH_SHORT).show();
            context.startActivity(intent);
            ((Activity) context).finish();
        }
    }
}
