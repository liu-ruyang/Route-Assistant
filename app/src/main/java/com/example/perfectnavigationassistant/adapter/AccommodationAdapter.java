package com.example.perfectnavigationassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perfectnavigationassistant.R;
import com.example.perfectnavigationassistant.Utils.DrawableUtils;
import com.example.perfectnavigationassistant.activity.DetailActivity;
import com.example.perfectnavigationassistant.bean.Accommodation;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AccommodationAdapter extends RecyclerView.Adapter<AccommodationAdapter.ViewHolder> {

    private List<Accommodation> acList;
    public Context con;

    public AccommodationAdapter(List<Accommodation> acList, Context con) {
        this.acList = acList;
        this.con = con;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ac_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.recommendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Accommodation accommodation = acList.get(position);
                Intent intent = new Intent(con, DetailActivity.class);
                Map<String, String> data = new HashMap<String, String>();
                data.put("img", accommodation.getAccommodationImg());
                data.put("name", accommodation.getName());
                data.put("location", accommodation.getLocation());
                data.put("city", accommodation.getCity());
                data.put("reason", accommodation.getReason());
                data.put("type", accommodation.getType());
                Set<String> keySet = data.keySet();
                Iterator<String> iter = keySet.iterator();
                Bundle bundle = new Bundle();
                while (iter.hasNext()) {
                    String key = iter.next();
                    bundle.putString(key, data.get(key));
                }
                intent.putExtra("Detail", bundle);
                con.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Accommodation accommodation = acList.get(position);
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
                URL acImgUrl = null;
                try {
                    acImgUrl = new URL(accommodation.getAccommodationImg());
                    HttpURLConnection conn = (HttpURLConnection) acImgUrl.openConnection();
                    conn.setConnectTimeout(0);
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream fis = conn.getInputStream();
                    Bitmap acImg = BitmapFactory.decodeStream(fis);
                    fis.close();
                    Bitmap roundAcImg = DrawableUtils.SetRoundCornerBitmap(acImg, 15);
                    Message message = new Message();
                    message.obj = roundAcImg;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        //thread.join();
        //holder.recommendImg.setImageResource(R.drawable.food_img_index);
        holder.recommendTitle.setText(accommodation.getName());
        holder.recommendAddress.setText(accommodation.getLocation());
    }

    @Override
    public int getItemCount() {
        return acList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View recommendView;
        ImageView recommendImg;
        TextView recommendTitle;
        TextView recommendAddress;

        public ViewHolder(View view) {
            super(view);
            recommendView = view;
            recommendImg = (ImageView) view.findViewById(R.id.ac_img);
            recommendTitle = (TextView) view.findViewById(R.id.ac_title);
            recommendAddress = (TextView) view.findViewById(R.id.ac_address);
        }
    }
}

