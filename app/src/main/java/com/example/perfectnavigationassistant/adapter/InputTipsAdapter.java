package com.example.perfectnavigationassistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perfectnavigationassistant.R;
import com.example.perfectnavigationassistant.pojo.Tip;

import java.util.List;

//输入提示适配器
public class InputTipsAdapter extends RecyclerView.Adapter<InputTipsAdapter.ViewHolder> {

    private List<Tip> tips;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View tip_list_item;
        TextView tv_placeName;
        TextView tv_placeDistrictAndAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tip_list_item = itemView;
            tv_placeName = itemView.findViewById(R.id.tv_placeName);
            tv_placeDistrictAndAddress = itemView.findViewById(R.id.tv_placeDistrictAndAddress);
        }
    }

    //实例化本适配器的时候，将要绑定的数据也传递过来
    public InputTipsAdapter(List<Tip> tips) {
        this.tips = tips;
    }

    //这里的参数parent在这里指的就是子项所放进的那个MyRecycleView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //拿到要添加的子控件，返回的是xml布局文件中的最外层布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tip_item, parent, false);
        //将子控件中需要的控件的id存到进ViewHolder中
        ViewHolder holder = new ViewHolder(view);
        //给子项最外层布局注册监听事件，此时，如果最外层布局的内部的其它控件没有注册监听事件，则将会触发最外层的注册的监听事件
        holder.tip_list_item.setOnClickListener(new View.OnClickListener() {
            //参数v是被点击的控件，在这里是每一个list_item项，它是两个textView的父控件
            @Override
            public void onClick(View v) {
                TextView clickView = v.findViewById(R.id.tv_placeName);
                String selectedPlaceName = clickView.getText().toString();

                View rootParent = (View) parent.getParent().getParent().getParent();

                //三个输入地点控件
                View startingPointList1 = rootParent.findViewById(R.id.starting_point_tips_list);
                View endPointList2 = rootParent.findViewById(R.id.end_point_tips_list);
                View newPlaceList3 = rootParent.findViewById(R.id.new_place_tips_list);

                if (startingPointList1.getVisibility() == View.VISIBLE) {
                    TextView startingPoint = rootParent.findViewById(R.id.starting_point);
                    startingPoint.setText(selectedPlaceName);
                    startingPointList1.setVisibility(View.INVISIBLE);

                } else if (endPointList2.getVisibility() == View.VISIBLE) {
                    TextView endPoint = rootParent.findViewById(R.id.end_point);
                    endPoint.setText(selectedPlaceName);
                    endPointList2.setVisibility(View.INVISIBLE);


                } else if (newPlaceList3.getVisibility() == View.VISIBLE) {
                    TextView newPlace = rootParent.findViewById(R.id.new_place);
                    newPlace.setText(selectedPlaceName);
                    newPlaceList3.setVisibility(View.INVISIBLE);
                }
//                int position = holder.getAdapterPosition();
//                Tip tip = tips.get(position);
//                System.out.println("触发RecycleView子项的点击事件" + tip);
            }
        });
        return holder;
    }

    //数据绑定到View的ViewHolder上面
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tip tip = tips.get(position);
        holder.tv_placeName.setText(tip.getPlaceName());
        holder.tv_placeDistrictAndAddress.setText(tip.getDistrictAndAddress());
    }

    @Override
    public int getItemCount() {
        //告诉RecycleView一共有多少个子项，直接返回数据源的长度
        return tips.size();
    }

}
