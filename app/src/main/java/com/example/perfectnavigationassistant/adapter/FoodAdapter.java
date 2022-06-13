package com.example.perfectnavigationassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perfectnavigationassistant.R;
import com.example.perfectnavigationassistant.Utils.DrawableUtils;
import com.example.perfectnavigationassistant.activity.DetailActivity;
import com.example.perfectnavigationassistant.bean.Food;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<Food> foodList;
    public Context con;

    public FoodAdapter(List<Food> foodList, Context con) {
        this.foodList = foodList;
        this.con = con;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.recommendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Food food = foodList.get(position);
//                Intent intent = new Intent("android.intent.action.DETAIL");
//                intent.addCategory("android.intent.category.DEFAULT");
                Intent intent = new Intent(con, DetailActivity.class);
                Map<String, String> data = new HashMap<String, String>();
                data.put("img", food.getFoodImg());
                data.put("name", food.getName());
                data.put("location", food.getLocation());
                data.put("city", food.getCity());
                data.put("reason", food.getReason());
                data.put("type", food.getType());
                Set<String> keySet = data.keySet();
                Iterator<String> iter = keySet.iterator();
                Bundle bundle = new Bundle();
                while (iter.hasNext()) {
                    String key = iter.next();
                    bundle.putString(key, data.get(key));
                }
                //SerializableMap map = new SerializableMap();
                //map.setMap(data);

                //bundle.putSerializable("foodMap", map);
                intent.putExtra("Detail", bundle);
                con.startActivity(intent);
            }
        });
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foodList.get(position);
        //holder.recommendImg.setImageResource(R.drawable.food_img_index);
        try {
            if (food.getFoodImg() == null) {
                Log.d("FileInputStream", "foodImg不存在");
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    try {
                        if (msg.obj != null) {
                            holder.recommendImg.setImageBitmap((Bitmap) msg.obj);
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
                        foodImgUrl = new URL(food.getFoodImg());
                        HttpURLConnection conn = (HttpURLConnection) foodImgUrl.openConnection();
                        conn.setConnectTimeout(0);
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream fis = conn.getInputStream();
                        Bitmap foodImg = BitmapFactory.decodeStream(fis);
                        fis.close();
                        Bitmap roundFoodImg = DrawableUtils.SetRoundCornerBitmap(foodImg, 15);
                        Message message = new Message();
                        message.obj = roundFoodImg;
                        handler.sendMessage(message);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            thread.start();
            //thread.join();

            // holder.recommendImg.setImageResource(R.drawable.food_img_index);
            holder.recommendTitle.setText(food.getName());
            holder.recommendAddress.setText(food.getLocation());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        View recommendView;
        ImageView recommendImg;
        TextView recommendTitle;
        TextView recommendAddress;


        public ViewHolder(View view) {
            super(view);
            recommendView = view;
            recommendImg = (ImageView) view.findViewById(R.id.food_img);
            recommendTitle = (TextView) view.findViewById(R.id.food_title);
            recommendAddress = (TextView) view.findViewById(R.id.food_address);

        }
    }
}
