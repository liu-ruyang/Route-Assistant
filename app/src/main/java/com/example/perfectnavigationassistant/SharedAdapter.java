package com.example.perfectnavigationassistant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perfectnavigationassistant.Utils.DrawableUtils;
import com.example.perfectnavigationassistant.bean.Dynamic;
import com.example.perfectnavigationassistant.bean.User;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class SharedAdapter extends RecyclerView.Adapter<SharedAdapter.MyViewHolder> {

    private Context context;
    private String Username, Publishtime, ConText, Userimg, Lovenumber;
    private BmobDate PublishTime;

    private int i;
    private List<Dynamic> dynamicList;

    //    构造函数
    public SharedAdapter(List<Dynamic> dynamiclist, Context context) {
        this.context = context;
        this.dynamicList = dynamiclist;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageview;
        private TextView username, publishtime, sharedtext, lovenumber;
        private ImageButton btn_love;
        Dynamic dynamicItem;
        Boolean flag = false;
        User user;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.imageview);
            username = itemView.findViewById(R.id.username);
            publishtime = itemView.findViewById(R.id.publishtime);
            sharedtext = itemView.findViewById(R.id.sharedtext);
            btn_love = (ImageButton) itemView.findViewById(R.id.btn_love);
            lovenumber = itemView.findViewById(R.id.lovenumber);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.dynamicItem = dynamicList.get(position);
        System.out.println(holder.dynamicItem);
//        用户名
        holder.user = dynamicList.get(position).getUserId();
        String Username = holder.user.getUsername();
        System.out.println(holder.user.toString());
        System.out.println(holder.user.getUsername());
        holder.username.setText(Username);
//        分享内容
        String ConText = dynamicList.get(position).getDynamicContent();
        holder.sharedtext.setText(ConText);
//        发布时间
        String Publishtime = dynamicList.get(position).getPublishTime().getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(Publishtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.publishtime.setText(date.getYear() + 1900 + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
//        获取用户头像
//        String Userimg = holder.user.getUserImg();
//        holder.imageview.setImageResource(R.drawable.img_1);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                try {
                    if (msg.obj != null) {
                        holder.imageview.setImageBitmap((Bitmap) msg.obj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL avatarUrl = null;
                try {
                    avatarUrl = new URL(holder.user.getUserImg());
                    HttpURLConnection conn = (HttpURLConnection) avatarUrl.openConnection();
                    conn.setConnectTimeout(0);
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream fis = conn.getInputStream();
                    Bitmap avatar = BitmapFactory.decodeStream(fis);
                    fis.close();
                    Bitmap roundAvatarImg = DrawableUtils.SetRoundCornerBitmap(avatar, 15);
                    Message message = new Message();
                    message.obj = roundAvatarImg;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
//        获取点赞数
        Integer Love = dynamicList.get(position).getLove();
        String Lovenumber = Love.toString();
        holder.lovenumber.setText(Lovenumber);

        holder.btn_love.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Integer love;
                if (!holder.flag) {
                    String LoveNumber = holder.lovenumber.getText().toString();
                    love = Integer.parseInt(LoveNumber);
                    love++;
                    holder.lovenumber.setText(love + "");
                    holder.btn_love.setBackground(context.getResources().getDrawable(R.drawable.love));
                    holder.dynamicItem.setLove(love);
                    String DynamicId = holder.dynamicItem.getObjectId();
                    holder.dynamicItem.update(DynamicId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                        }
                    });
                    holder.flag = !(holder.flag);
                } else {
                    System.out.println(context.getResources().getDrawable(R.drawable.index_love));
                    System.out.println(holder.btn_love.getBackground());

                    String LoveNumber = holder.lovenumber.getText().toString();
                    love = Integer.parseInt(LoveNumber);
                    love--;
                    holder.lovenumber.setText(love + "");
                    holder.btn_love.setBackground(context.getResources().getDrawable(R.drawable.index_love));
                    holder.dynamicItem.setLove(love);
                    String DynamicId = holder.dynamicItem.getObjectId();
                    holder.dynamicItem.update(DynamicId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                        }
                    });
                    holder.flag = !(holder.flag);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return dynamicList.size();
    }


}
