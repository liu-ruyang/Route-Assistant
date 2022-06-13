package com.example.perfectnavigationassistant.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.perfectnavigationassistant.R;
import com.example.perfectnavigationassistant.Utils.DrawableUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DetailActivity extends AppCompatActivity {

    TextView detailName;
    TextView detailAddress;
    TextView detailReason;
    ImageView detailImg;
    TextView detailCity;
    private Map<String, String> foodMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }

        detailImg = findViewById(R.id.detailImg);
        detailName = findViewById(R.id.detailName);
        detailAddress = findViewById(R.id.detailAddress);
        detailReason = findViewById(R.id.detailReason);
        detailCity = findViewById(R.id.detailCity);

        //接收数据
        Intent intent = getIntent();
        foodMap = new HashMap<>();
        Bundle bundle = intent.getBundleExtra("Detail");
        Set<String> keySet = bundle.keySet();
        Iterator<String> iter = keySet.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            foodMap.put(key, bundle.getString(key));
        }
        //Bundle bundle1=intent.getBundleExtra("placeDetail");
//        if (null == bundle) {
//            Log.d("bundle", "bundle为空");
//        } else {
//            Log.d("bundle", "bundle：" + bundle);
//        }

//        if (null == foodMap) {
//            Log.d("foodMap", "foodMap为空");
//        } else {
//            Log.d("foodMap", "foodMap的name：" + foodMap.get("name"));
//        }
//        Log.d("name", foodMap.get("name"));
//        detailImg.setImageURI(Uri.fromFile(new File(foodMap.get("foodImg"))));

        Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {

                try {
                    if (msg.obj != null) {
                        detailImg.setImageBitmap((Bitmap) msg.obj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL foodImgUrl = null;
                try {
                    foodImgUrl = new URL(foodMap.get("img"));
                    HttpURLConnection conn = (HttpURLConnection) foodImgUrl.openConnection();
                    conn.setConnectTimeout(0);
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream fis = conn.getInputStream();
                    Bitmap img = BitmapFactory.decodeStream(fis);
                    fis.close();
                    Bitmap roundImg = DrawableUtils.SetRoundCornerBitmap(img, 15);
                    Message message = new Message();
                    message.obj = roundImg;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        //显示数据
        detailName.setText(foodMap.get("name"));
        detailAddress.setText(foodMap.get("location"));
        detailCity.setText(foodMap.get("city") + "市");
        detailReason.setText(foodMap.get("reason"));
    }
}