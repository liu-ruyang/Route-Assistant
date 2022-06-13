package com.example.perfectnavigationassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perfectnavigationassistant.bean.Dynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

//public class SharedActivity extends AppCompatActivity {
public class SharedActivity {
    View viewItem = null;
    Context context = null;
    //================================
    private RecyclerView list;
    private Button btn_added;
    private String Username;
    private byte[] myAvatars;
    private List<Dynamic> LIST;
    private SharedAdapter sharedAdapter;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_shared);
//
//        Init();
////        发布按钮
//        btn_added.setOnClickListener(new AddedButtonListener());
//    }

    public void start(Context context, View parentView) {
        this.context = context;
        viewItem = parentView;

        Init();
//        发布按钮
        btn_added.setOnClickListener(new AddedButtonListener());
    }

    public void Init() {
        list = (RecyclerView) viewItem.findViewById(R.id.recyclerview);
        btn_added = (Button) viewItem.findViewById(R.id.btn_added);
        Bmob.initialize(context, "be29b9fb7187aa9f260f86cc427473ed");

        Intent intent = ((Activity) context).getIntent();
        Username = intent.getStringExtra("username");
        myAvatars = intent.getByteArrayExtra("myAvatar");
//        Toast.makeText(context, "当前username:" + Username, Toast.LENGTH_SHORT).show();

        //从数据库中获取信息
        BmobQuery<Dynamic> dynamicBmobQuery = new BmobQuery<Dynamic>();
        dynamicBmobQuery.include("userId");
//        System.out.println("开始查询数据库");
        dynamicBmobQuery.findObjects(new FindListener<Dynamic>() {
            @Override
            public void done(List<Dynamic> dynamiclist, BmobException e) {
                LIST = new ArrayList<>();
//                System.out.println("查询数据库结束");
                if (null == e) {
                    for (Dynamic dynamic : dynamiclist) {
                        LIST.add(dynamic);
                    }
//                    List<Dynamic> LISTReverse = new ArrayList<>();
//                    for (int i = LIST.size() - 1; i >= 0; i--) {
//                        LISTReverse.add(LIST.get(i));
//                    }
                    Collections.reverse(LIST);
//                    System.out.println(LIST);
//                    System.out.println(LISTReverse);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    sharedAdapter = new SharedAdapter(LIST, context);
                    list.setLayoutManager(layoutManager);
                    list.setAdapter(sharedAdapter);
                }
            }
        });

    }

    //    新增发布按钮
    class AddedButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, AddedActivity.class);
            intent.putExtra("username", Username);
            intent.putExtra("myAvatar", myAvatars);
//            System.out.println(Username);
            context.startActivity(intent);
        }
    }
}
