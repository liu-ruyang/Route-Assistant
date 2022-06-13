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
import com.example.perfectnavigationassistant.bean.Place;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private List<Place> placeList;
    public Context con;

    public PlaceAdapter(List<Place> placeList, Context con) {

        this.placeList = placeList;
        this.con = con;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.recommendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Place place = placeList.get(position);
                Intent intent = new Intent(con, DetailActivity.class);
                Map<String, String> data = new HashMap<String, String>();
                data.put("img", place.getPlaceImg());
                data.put("name", place.getName());
                data.put("location", place.getLocation());
                data.put("city", place.getCity());
                data.put("reason", place.getReason());
                data.put("type", place.getType());
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
        Place place = placeList.get(position);
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
                URL placeImgUrl = null;
                try {
                    placeImgUrl = new URL(place.getPlaceImg());
                    HttpURLConnection conn = (HttpURLConnection) placeImgUrl.openConnection();
                    conn.setConnectTimeout(0);
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream fis = conn.getInputStream();
                    Bitmap placeImg = BitmapFactory.decodeStream(fis);
                    fis.close();
                    Bitmap roundPlaceImg = DrawableUtils.SetRoundCornerBitmap(placeImg, 15);
                    Message message = new Message();
                    message.obj = roundPlaceImg;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        //thread.join();
        //holder.recommendImg.setImageResource(R.drawable.food_img_index);
        holder.recommendTitle.setText(place.getName());
        holder.recommendAddress.setText(place.getLocation());
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View recommendView;
        ImageView recommendImg;
        TextView recommendTitle;
        TextView recommendAddress;


        public ViewHolder(View view) {
            super(view);
            recommendView = view;
            recommendImg = (ImageView) view.findViewById(R.id.place_img);
            recommendTitle = (TextView) view.findViewById(R.id.place_title);
            recommendAddress = (TextView) view.findViewById(R.id.place_address);

        }
    }
}
