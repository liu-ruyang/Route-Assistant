package com.example.perfectnavigationassistant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.NaviSetting;
import com.amap.api.navi.enums.PageType;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviPoi;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.perfectnavigationassistant.activity.RecommendationActivity;
import com.example.perfectnavigationassistant.adapter.InputTipsAdapter;
import com.example.perfectnavigationassistant.mylistener.MyAMapNaviListener;
import com.example.perfectnavigationassistant.myview.HorizantalAutoBrLayout;
import com.example.perfectnavigationassistant.myview.MaxHeightRecyclerView;
import com.example.perfectnavigationassistant.pojo.Order;
import com.example.perfectnavigationassistant.pojo.Place;
import com.example.perfectnavigationassistant.pojo.Tip;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;

public class MainActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener, LocationSource {

    Button btn_show_route;
    Button btn_primary_ok;
    Button btn_test_locate;
    Button btn_test_nav;
    Button btn_test_poi;
    Button btn_advanced_open;
    Button btn_add_place;
    Button btn_clear_allPlaces;
    Button btn_back_to_before;
    Button btn_advanced_ok;
    Button btn_clear_all_routes_planed;
    RadioGroup radio_strategy_advanced;
    RadioButton radio_shortestDistance_advanced;
    RadioButton radio_shortestTime_advanced;
    RadioGroup radio_strategy_pramary;
    RadioButton radio_shortestTime_primary;
    RadioButton radio_shortestDistance_primary;
    ConstraintLayout primary_search_box;
    ConstraintLayout advanced_dialog;
    HorizantalAutoBrLayout display_places;
    EditText starting_point;
    EditText end_point;
    EditText new_place;
    TextView tv_time;
    TextView tv_distance;
    View starting_point_tips_list;
    View end_point_tips_list;
    View new_place_tips_list;
    MaxHeightRecyclerView starting_point_tipsListView;
    MaxHeightRecyclerView end_point_tipsListView;
    MaxHeightRecyclerView new_place_tipsListView;
    View part_navigation;

    //底部导航栏
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private RadioButton tab1, tab2, tab3, tab4;  //四个单选按钮
    private List<View> mViews;   //存放视图
    private View viewItem;
    private int global_position;

    //用于调用地图初始化方法
    private Bundle mySavedInstanceState;

    //动态添加途径点所在控件的id
    private static int id = 1;
    private boolean flag = false;
    private int search_type = 0;
    Map poi = null;
    Map resultMap = null;
    List places = new ArrayList<String>();//第0个点作为起点：起点不会

    // Response response = null;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private UiSettings mUiSettings;//定义一个UiSettings对象
    AMap aMap = null;
    AMapNavi mAMapNavi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mySavedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "be29b9fb7187aa9f260f86cc427473ed");
        //setContentView(R.layout.homepage);
        setContentView(R.layout.index);
        //将系统自带的标题栏隐藏掉（只隐藏当前的Activity）
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }

        mViewPager = findViewById(R.id.viewpager);
        mRadioGroup = findViewById(R.id.rg_tab);
        tab1 = findViewById(R.id.rb_routing);
        tab2 = findViewById(R.id.rb_recommend);
        tab3 = findViewById(R.id.rb_dynamic);
        tab4 = findViewById(R.id.rb_me);

        //加载，添加视图
        mViews = new ArrayList<View>();
        mViews.add(LayoutInflater.from(this).inflate(R.layout.homepage, null));
        mViews.add(LayoutInflater.from(this).inflate(R.layout.activity_recommendation, null));
        mViews.add(LayoutInflater.from(this).inflate(R.layout.activity_shared, null));
        mViews.add(LayoutInflater.from(this).inflate(R.layout.activity_mine, null));

        mViewPager.setAdapter(new MyViewPagerAdapter());//设置一个适配器
        //对viewpager监听，让分页和底部图标保持一起滑动
        //对viewpager监听，让分页和底部图标保持一起滑动
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override   //让viewpager滑动的时候，下面的图标跟着变动
            public void onPageSelected(int position) {
                //
                global_position = position;
                switch (position) {
                    case 0:
                        tab1.setChecked(true);
                        tab2.setChecked(false);
                        tab3.setChecked(false);
                        tab4.setChecked(false);
                        break;
                    case 1:
                        tab1.setChecked(false);
                        tab2.setChecked(true);
                        tab3.setChecked(false);
                        tab4.setChecked(false);
                        break;
                    case 2:
                        tab1.setChecked(false);
                        tab2.setChecked(false);
                        tab3.setChecked(true);
                        tab4.setChecked(false);
                        break;
                    case 3:
                        tab1.setChecked(false);
                        tab2.setChecked(false);
                        tab3.setChecked(false);
                        tab4.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                System.out.println("onPageScrollStateChanged(" + state + ")");
            }
        });
        //对单选按钮进行监听，选中、未选中
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_routing:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_recommend:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_dynamic:
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.rb_me:
                        mViewPager.setCurrentItem(3);
                        break;
                }
            }
        });

        //开启定位功能
//        openGPS();
        //开启导航功能
//        nav();
    }

    //内部类：ViewPager适配器
    private class MyViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(mViews.get(position));
        }

        //预加载：PagerAdapter有预加载的机制，会默认预加载当前加载View的前一个和后一个View，
        //缓存View数量：所以，PagerAdapter可一共可以缓存三张View
        //View销毁机制：因此，当PagerAdapter在已经缓存了三张View的时候，再去加载一个新的View，则会调用上面的DestroyItem()方法，将缓存的第一个View销毁
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(mViews.get(position));
            viewItem = mViews.get(position);
//            global_position = position;
//            Toast.makeText(MainActivity.this, "positoin:" + position, Toast.LENGTH_SHORT).show();
            switch (position) {
                case 0:
                    bindViews();
                    //开启一些监听事件（包括测试监听）
                    addListenners();
                    break;
                case 1:
                    RecommendationActivity recommendationActivity = new RecommendationActivity();
                    recommendationActivity.start(MainActivity.this, viewItem);
                    break;
                case 2:
                    SharedActivity sharedActivity = new SharedActivity();
                    sharedActivity.start(MainActivity.this, viewItem);
                    break;
                case 3:
                    MineActivity mineActivity = new MineActivity();
                    mineActivity.start(MainActivity.this, viewItem);
                    break;
            }

            return mViews.get(position);
        }
    }

    //用于“我的”界面接收修改后的数据，进行更新UI
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data && null != data.getStringExtra("from") && (data.getStringExtra("from")).equals("information")) {
            Intent intent = getIntent();
            intent.putExtra("username", data.getStringExtra("username"));
//            Toast.makeText(this, "更新了userName，准备跟新UI", Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "global_position:" + global_position, Toast.LENGTH_SHORT).show();
            if (global_position == 3) {
//                TextView username = (TextView) viewItem.findViewById(R.id.username);
//                Toast.makeText(this, "更新了userName", Toast.LENGTH_SHORT).show();
//                username.setText(intent.getStringExtra("username"));
            }
        }
    }

    //===========刘汝杨：功能API===========
    //绑定视图，包括地图的初始化
    void bindViews() {
//        System.out.println("绑定控件");
        btn_show_route = viewItem.findViewById(R.id.btn_show_route);
        btn_primary_ok = viewItem.findViewById(R.id.btn_primary_ok);
        starting_point = viewItem.findViewById(R.id.starting_point);
        end_point = viewItem.findViewById(R.id.end_point);
        btn_test_locate = viewItem.findViewById(R.id.btn_test_locate);
        btn_test_nav = viewItem.findViewById(R.id.btn_test_nav);
        btn_test_poi = viewItem.findViewById(R.id.btn_test_poi);
        btn_advanced_open = viewItem.findViewById(R.id.btn_advanced_open);
        btn_add_place = viewItem.findViewById(R.id.btn_add_place);
        btn_clear_allPlaces = viewItem.findViewById(R.id.btn_clear_allPlaces);
        btn_back_to_before = viewItem.findViewById(R.id.btn_back_to_before);
        btn_advanced_ok = viewItem.findViewById(R.id.btn_advanced_ok);
        radio_strategy_advanced = viewItem.findViewById(R.id.radio_strategy_advanced);
        radio_shortestDistance_advanced = viewItem.findViewById(R.id.radio_shortestDistance_advanced);
        radio_shortestTime_advanced = viewItem.findViewById(R.id.radio_shortestTime_advanced);
        radio_strategy_pramary = viewItem.findViewById(R.id.radio_strategy_primary);
        radio_shortestTime_primary = viewItem.findViewById(R.id.radio_shortestTime_primary);
        radio_shortestDistance_primary = viewItem.findViewById(R.id.radio_shortestDistance_primary);
        advanced_dialog = viewItem.findViewById(R.id.advanced_dialog);
        primary_search_box = viewItem.findViewById(R.id.primary_search_box);
        display_places = viewItem.findViewById(R.id.display_places);
        new_place = viewItem.findViewById(R.id.new_place);
        tv_time = viewItem.findViewById(R.id.tv_time);
        tv_distance = viewItem.findViewById(R.id.tv_distance);
        btn_clear_all_routes_planed = viewItem.findViewById(R.id.btn_clear_all_routes_planed);
        starting_point_tips_list = viewItem.findViewById(R.id.starting_point_tips_list);
        end_point_tips_list = viewItem.findViewById(R.id.end_point_tips_list);
        new_place_tips_list = viewItem.findViewById(R.id.new_place_tips_list);
        part_navigation = viewItem.findViewById(R.id.part_navigation);
        starting_point_tipsListView = starting_point_tips_list.findViewById(R.id.tips_list);
        end_point_tipsListView = end_point_tips_list.findViewById(R.id.tips_list);
        new_place_tipsListView = new_place_tips_list.findViewById(R.id.tips_list);

        //初始化地图
        mapInit(mySavedInstanceState);

        //开启本机定位（仅定位一次）
//        openGPS();
        //停止本机定位
//        System.out.println("停止本机定位");
//        stopGPS();
    }

    //添加监听事件
    void addListenners() {
        //下拉菜单
        btn_show_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (primary_search_box.getVisibility() == View.INVISIBLE && advanced_dialog.getVisibility() == View.INVISIBLE) {
                    primary_search_box.setVisibility(View.VISIBLE);
                    btn_show_route.setBackgroundResource(R.drawable.up_list);
                } else {
                    primary_search_box.setVisibility(View.INVISIBLE);
                    btn_show_route.setBackgroundResource(R.drawable.down_list);
                }
            }
        });

        //两地之间获取最佳路径：两种角度
        btn_primary_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != starting_point && null != end_point && !(starting_point.getText().toString().equals("")) && !(end_point.getText().toString().equals(""))) {
//                    System.out.println("开始规划");
                    //先记录当前的搜索类型，0代表初级路线规划，1代表高级路线规划
                    search_type = 0;
                    //先清除之前的规划路线
                    aMap.clear();
                    tv_time.setText("");
                    tv_distance.setText("");

                    String startPlace = starting_point.getText().toString().trim();
                    String endPlace = end_point.getText().toString().trim();
                    //获取两地各自的坐标
                    Place start = new Place();
                    Place end = new Place();

                    Map poiMap = getPOI(startPlace);
                    start.setName((String) poiMap.get("name"));
                    start.setPoiid((String) poiMap.get("poiid"));
                    start.setLongitude((String) poiMap.get("longitude"));
                    start.setLatitude((String) poiMap.get("latitude"));

                    poiMap = getPOI(endPlace);
                    end.setName((String) poiMap.get("name"));
                    end.setPoiid((String) poiMap.get("poiid"));
                    end.setLongitude((String) poiMap.get("longitude"));
                    end.setLatitude((String) poiMap.get("latitude"));

                    flag = false;

                    //获取路程最佳的路径
//                bestLengthRoute(start, end);
                    //获取时间最佳的路径
                    if (radio_strategy_pramary.getCheckedRadioButtonId() == radio_shortestTime_primary.getId()) {
                        bestTimeRoute(start, end);
                    } else {
                        bestLengthRoute(start, end);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "起点和终点不能有空值", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //打开高级搜索弹窗
        btn_advanced_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                primary_search_box.setVisibility(View.INVISIBLE);
                advanced_dialog.setVisibility(View.VISIBLE);

                //禁用下拉框点击特性
                btn_show_route.setEnabled(false);
            }
        });

        //添加途径点
        btn_add_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPlace = new_place.getText().toString().trim();
                new_place.setText("");

//                //添加一个TextView
//                TextView textView = new TextView(MainActivity.this);
//                textView.setId(id++);
//                textView.setText(newPlace);
//                textView.setPadding(10, 2, 10, 2);
//                //setWidth只代表想设置的宽，并不是实际设定值，如果有默认值，则需要大于默认值才会起作用
////                textView.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
//                //setHeight只代表想设置的高，并不是实际设定值，如果有默认值，则需要大于默认值才会起作用
////                textView.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
//                //用于告诉父控件，子控件需要被设置的布局参数信息
//                HorizantalAutoBrLayout.LayoutParams params = new HorizantalAutoBrLayout
//                        .LayoutParams(HorizantalAutoBrLayout.LayoutParams.WRAP_CONTENT,
//                        HorizantalAutoBrLayout.LayoutParams.WRAP_CONTENT);
//                textView.setLayoutParams(params);
//                display_places.addView(textView);
//
//                //将新添加的地点名称添加到List数组中
//                places.add(newPlace);
//添加一个TextView

                //setWidth只代表想设置的宽，并不是实际设定值，如果有默认值，则需要大于默认值才会起作用
//                textView.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                //setHeight只代表想设置的高，并不是实际设定值，如果有默认值，则需要大于默认值才会起作用
//                textView.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                //用于告诉父控件，子控件需要被设置的布局参数信息
                HorizantalAutoBrLayout.LayoutParams params = new HorizantalAutoBrLayout
                        .LayoutParams(HorizantalAutoBrLayout.LayoutParams.WRAP_CONTENT,
                        HorizantalAutoBrLayout.LayoutParams.WRAP_CONTENT);

                LinearLayout place_box = new LinearLayout(getApplicationContext());
                place_box.setOrientation(LinearLayout.HORIZONTAL);
                place_box.setLayoutParams(params);

                LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 60);
                imgParams.leftMargin = 5;
                imgParams.weight = 1;
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setImageResource(R.drawable.place_location);
                imageView.setLayoutParams(imgParams);

                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textParams.weight = 3;
                textParams.gravity = Gravity.CENTER_VERTICAL;
                TextView textView = new TextView(MainActivity.this);
                textView.setId(id++);
                textView.setText(newPlace);
                textView.setPadding(10, 2, 10, 2);
                textView.setLayoutParams(textParams);

                place_box.addView(imageView, 0);
                place_box.addView(textView, 1);
                display_places.addView(place_box);

                //将新添加的地点名称添加到List数组中
                places.add(newPlace);
//                System.out.println("第一次添加，添加的内容是" + newPlace);
            }
        });

        //清空所有途径点
        btn_clear_allPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_places.removeAllViews();
                new_place.setText("");
                places.clear();
                display_places.haveChild = false;
            }
        });

        //路线规划成功后，需要将路线显示出来
        //添加监听器，这里使用自定义的继承自AMapNaviListener接口的类，应为AMapNaviListener接口需要实现的方法太多，写在一个类中显得太拥挤
        //路线规划成功时，会触发 AMapNaviListener 的 onCalculateRouteSuccess 回调，在该回调函数中，可以获取路线对象，进行规划路线的显示
        mAMapNavi.addAMapNaviListener(new MyAMapNaviListener() {
            @Override
            public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

                // 获取路线数据对象
                HashMap<Integer, AMapNaviPath> naviPaths = null;
                try {
                    naviPaths = AMapNavi.getInstance(MainActivity.this).getNaviPaths();
                } catch (AMapException e) {
                    e.printStackTrace();
                }
                int[] routeid = aMapCalcRouteResult.getRouteid();
                AMapNaviPath aMapNaviPath = naviPaths.get(routeid[0]);

                // 打印出时间、路程数据
                int allTime = aMapNaviPath.getAllTime();
                int allLength = aMapNaviPath.getAllLength();
                if (search_type == 0) {
//                    System.out.println("advanced_dialog.getVisibility() == View.INVISIBLE");
//                    System.out.println("时间：" + allTime);
//                    System.out.println("路程：" + allLength);
                    if (radio_strategy_pramary.getCheckedRadioButtonId() == radio_shortestTime_primary.getId()) {
                        tv_time.setText("最短时间：" + allTime * 1.0 / 60 + "min");
                        tv_distance.setText("路程：" + allLength * 1.0 / 1000 + "km");
                    } else {
                        tv_time.setText("时间：" + allTime * 1.0 / 60 + "min");
                        tv_distance.setText("最短路程：" + allLength * 1.0 / 1000 + "km");
                    }
//                    tv_time.setText("时间：" + allTime * 1.0 / 60 + "min");
//                    tv_distance.setText("路程：" + allLength * 1.0 / 1000 + "km");
                    tv_time.setVisibility(View.VISIBLE);
                    tv_distance.setVisibility(View.VISIBLE);
                } else if (radio_strategy_advanced.getCheckedRadioButtonId() == radio_shortestTime_advanced.getId()) {
//                    System.out.println("radio_strategy_advanced.getCheckedRadioButtonId() == radio_shortestTime_advanced.getId()");
//                    System.out.println("采用最短耗时，得出路线结果：");
//                    System.out.println("最短时间：" + allTime);
//                    System.out.println("路程：" + allLength);
                    tv_time.setText("最短时间：" + allTime * 1.0 / 60 + "min");
                    tv_distance.setText("路程：" + allLength * 1.0 / 1000 + "km");
                    tv_time.setVisibility(View.VISIBLE);
                    tv_distance.setVisibility(View.VISIBLE);
                } else {
//                    System.out.println("else");
//                    System.out.println("采用最短距离，得出路线结果：");
//                    System.out.println("时间：" + allTime);
//                    System.out.println("最短路程：" + allLength);
                    tv_time.setText("时间：" + allTime * 1.0 / 60 + "min");
                    tv_distance.setText("最短路程：" + allLength * 1.0 / 1000 + "km");
                    tv_time.setVisibility(View.VISIBLE);
                    tv_distance.setVisibility(View.VISIBLE);
                }

                RouteOverLay routeOverLay = new RouteOverLay(aMap, aMapNaviPath, MainActivity.this);

                //添加到AMapNaviView上（即把路线显示到地图上）
                routeOverLay.addToMap();

//                System.out.println("路线规划成功");
                Toast.makeText(MainActivity.this, "路线规划成功", Toast.LENGTH_SHORT).show();

                // 开启导航
//                try {
//                    AMapNavi.getInstance(MainActivity.this).startNavi(NaviType.GPS);
//                    System.out.println("开启导航");
//                } catch (AMapException e) {
//                    e.printStackTrace();
//                }
            }
        });

        //高级搜索确认
        btn_advanced_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (display_places.getChildCount() < 2) {
                    Toast.makeText(MainActivity.this, "至少需要两个点", Toast.LENGTH_SHORT).show();
                } else if (display_places.getChildCount() >= 2) {
//                    System.out.println("监听到确认开启高级搜索");
                    //先记录当前的搜索类型，0代表初级路线规划，1代表高级路线规划
                    search_type = 1;
                    //先清除之前的规划路线
                    aMap.clear();
                    tv_time.setText("");
                    tv_distance.setText("");
                    starting_point.setText("");
                    end_point.setText("");
                    //打印出所有途经点
//                    System.out.println("所有经过的地点：" + places.toString());
                    int[][] dataTable = null;
                    if (radio_shortestTime_advanced.isChecked()) {
//                        System.out.println("时间最少策略");
                        dataTable = getDataTable(places, PathPlanningStrategy.DRIVING_DEFAULT);
                    } else if (radio_shortestDistance_advanced.isChecked()) {
//                        System.out.println("路程最短策略");
                        dataTable = getDataTable(places, PathPlanningStrategy.DRIVING_SHORTEST_DISTANCE);
                    }
                    /*规划路线，并画出路线*/
                    Order order = MyUtil.bestOrder(dataTable);
                    int[] bestOrder = order.getBestOrder();//最好的出行顺序
                    int amountOfOrders = order.getAmountOfOrders();//一共可以有的方案数
                    int minValue = order.getMinValue();//最少需要的路程、或时间

                    //计算好起点、终点、以及途径点之后，本机只需要一次网络请求，耗时短
//                    System.out.println("--------------开始获取各地点的POI信息-----------------");
                    ArrayList<NaviPoi> waysPoiIds = new ArrayList<NaviPoi>();
                    NaviPoi point = null;
                    NaviPoi first = null;
                    NaviPoi last = null;
                    Map poi = null;

                    poi = getPOI((String) places.get(0));
                    first = new NaviPoi((String) places.get(0), null, (String) poi.get("poiid"));

                    for (int i = 0; i < bestOrder.length; i++) {
                        if (i == bestOrder.length - 1) {
                            poi = getPOI((String) places.get(bestOrder[i]));
                            last = new NaviPoi((String) places.get(bestOrder[i]), null, (String) poi.get("poiid"));
                        } else {
                            poi = getPOI((String) places.get(bestOrder[i]));
                            point = new NaviPoi((String) places.get(bestOrder[i]), null, (String) poi.get("poiid"));
                            waysPoiIds.add(point);
                        }
                    }
                    multPointBestRoute(first, last, waysPoiIds);

                    //-----------------------------------
                    //两两点之间进行规划路线，本机需要很多次网络请求，耗时长
//                Place start = null;
//                Place end = null;
//
//                Boolean firstDrawed = false;//firstDrawed为false，表示第一段路程还没有画出
//                for (int i = 0; i < bestOrder.length; ) {
//                    start = new Place();
//                    end = new Place();
//                    if (!firstDrawed) {//第一个起点开始的第一段路程
//                        String startPlace = (String) places.get(0);
//                        String endPlace = (String) places.get(bestOrder[i]);
//                        Map poitest = getPOI(startPlace);
//                        start.setName((String) poitest.get("name"));
//                        start.setPoiid((String) poitest.get("poiid"));
//                        start.setLongitude((String) poitest.get("longitude"));
//                        start.setLatitude((String) poitest.get("latitude"));
//
//                        poitest = getPOI(endPlace);
//                        end.setName((String) poitest.get("name"));
//                        end.setPoiid((String) poitest.get("poiid"));
//                        end.setLongitude((String) poitest.get("longitude"));
//                        end.setLatitude((String) poitest.get("latitude"));
//
//                        //第一段已画出
//                        firstDrawed = true;
//
//                        System.out.println("第一段：" + start.getName() + "------->" + end.getName());
//                    } else if (i < bestOrder.length - 1) {
//                        String startplace = (String) places.get(bestOrder[i]);
//                        String endPlace = (String) places.get(bestOrder[i + 1]);
//                        Map poitest = getPOI(startplace);
//                        start.setName((String) poitest.get("name"));
//                        start.setPoiid((String) poitest.get("poiid"));
//                        start.setLongitude((String) poitest.get("longitude"));
//                        start.setLatitude((String) poitest.get("latitude"));
//
//                        poitest = getPOI(endPlace);
//                        end.setName((String) poitest.get("name"));
//                        end.setPoiid((String) poitest.get("poiid"));
//                        end.setLongitude((String) poitest.get("longitude"));
//                        end.setLatitude((String) poitest.get("latitude"));
//
//                        i++;//画非第一段路程的时候，需要将i自增
//
//                        System.out.println("非第一段：" + start.getName() + "------->" + end.getName());
//                    } else {
//                        break;
//                    }
//
//                    System.out.println("开始批量规划路线");
//                    if (radio_shortestTime_advanced.isChecked()) {
//                        System.out.println("用时间最短策略规划线路");
//                        bestTimeRoute(start, end);
//                        System.out.println("已画出第 " + (++count) + " 段路程");
//                        tv_time.setVisibility(View.VISIBLE);
//                        tv_time.setText("时间：" + minValue * 1.0 / 60 + "min");
//                        tv_distance.setText("");
//                        tv_distance.setVisibility(View.INVISIBLE);
//                    } else if (radio_shortestDistance_advanced.isChecked()) {
//                        System.out.println("用路程最短策略规划路线");
//                        bestLengthRoute(start, end);
//                        System.out.println("已画出第 " + (++count) + " 段路程");
//                        tv_time.setVisibility(View.VISIBLE);
//                        tv_time.setText("路程：" + minValue * 1.0 / 1000 + "km");
//                        tv_distance.setText("");
//                        tv_distance.setVisibility(View.INVISIBLE);
//                    }
//
//                }
                    //-----------------------------------

                    places.clear();
                    display_places.removeAllViews();
                    display_places.haveChild = false;
                    new_place.setText("");
                    primary_search_box.setVisibility(View.VISIBLE);
                    advanced_dialog.setVisibility(View.INVISIBLE);

                    //恢复下拉框点击特性
                    btn_show_route.setEnabled(true);
                }
            }
        });

        //取消高级搜索
        btn_back_to_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除所有子控件
                display_places.removeAllViews();
                display_places.haveChild = false;
                new_place.setText("");
                places.clear();

                primary_search_box.setVisibility(View.VISIBLE);
                advanced_dialog.setVisibility(View.INVISIBLE);

                //恢复下拉框点击特性
                btn_show_route.setEnabled(true);
            }
        });

        //清空规划路线
        btn_clear_all_routes_planed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.clear();
                tv_time.setText("");
                tv_distance.setText("");
            }
        });

        //给三个EditText添加输入提示监听器
        starting_point.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //调用输入提示API，将结果赋给List<Tip>对象
                String text = starting_point.getText().toString().trim();
                //搜索框中有内容
                if (null != text && !(text.equals("")) && starting_point.isFocused()) {
                    //输入提示API调用
                    String uri = "https://restapi.amap.com/v3/assistant/inputtips?" +
                            "key=" + "6d61a5918c86aaaeb6802c14133a8519" + "&" +
                            "keywords=" + text + "&" +
                            "output=" + "json" + "&" +
                            "datatype=" + "all";
                    List tipsList = new ArrayList<Tip>();

                    //使用HttPURLConnection进行网络请求
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            System.out.println("执行子线程");
                            HttpURLConnection connection = null;
                            BufferedReader reader = null;
                            InputStream inputStream = null;
                            InputStreamReader inputStreamReader = null;
                            try {
                                URL url = new URL(uri);
                                connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");
                                connection.setConnectTimeout(8000);
                                connection.setReadTimeout(8000);
                                inputStream = connection.getInputStream();
                                //下面对获取到的输入流进行读取
                                inputStreamReader = new InputStreamReader(inputStream);

                                reader = new BufferedReader(inputStreamReader);
                                StringBuilder response = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    response.append(line);
                                }

                                //将json字符串解析出来
                                JSONObject jsonObject = JSONObject.parseObject(response.toString(), JSONObject.class);
                                JSONArray tips = jsonObject.getJSONArray("tips");
                                if (tips.size() > 0) {
                                    for (Object tip : tips) {
                                        JSONObject jsonTip = (JSONObject) tip;
                                        String name = jsonTip.getString("name");
                                        String district = jsonTip.getString("district");
                                        String address = jsonTip.getString("address");
                                        String poiId = jsonTip.getString("id");
                                        tipsList.add(new Tip(name, poiId, district + address));
                                    }
                                } else {
                                    tipsList.add(new Tip("空", "", ""));
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    reader.close();
                                    inputStream.close();
                                    inputStreamReader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
//                            System.out.println("子线程执行结束");
                        }
                    });
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //显示到控件上
//                    MaxHeightRecyclerView starting_point_tipsListView = starting_point_tips_list.findViewById(R.id.tips_list);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                    starting_point_tipsListView.setLayoutManager(layoutManager);
                    InputTipsAdapter inputTipsAdapter = new InputTipsAdapter(tipsList);
                    starting_point_tipsListView.setAdapter(inputTipsAdapter);
                    starting_point_tips_list.setVisibility(View.VISIBLE);
                } else {
                    starting_point_tips_list.setVisibility(View.INVISIBLE);
                }
            }
        });
        end_point.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //调用输入提示API，将结果赋给List<Tip>对象
                String text = end_point.getText().toString().trim();
                //搜索框中有内容
                if (null != text && !(text.equals("")) && end_point.isFocused()) {
                    //输入提示API调用
                    String uri = "https://restapi.amap.com/v3/assistant/inputtips?" +
                            "key=" + "6d61a5918c86aaaeb6802c14133a8519" + "&" +
                            "keywords=" + text + "&" +
                            "output=" + "json" + "&" +
                            "datatype=" + "all";
                    List tipsList = new ArrayList<Tip>();

                    //使用HttPURLConnection进行网络请求
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            System.out.println("执行子线程");
                            HttpURLConnection connection = null;
                            BufferedReader reader = null;
                            InputStream inputStream = null;
                            InputStreamReader inputStreamReader = null;
                            try {
                                URL url = new URL(uri);
                                connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");
                                connection.setConnectTimeout(8000);
                                connection.setReadTimeout(8000);
                                inputStream = connection.getInputStream();
                                //下面对获取到的输入流进行读取
                                inputStreamReader = new InputStreamReader(inputStream);

                                reader = new BufferedReader(inputStreamReader);
                                StringBuilder response = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    response.append(line);
                                }

                                //将json字符串解析出来
                                JSONObject jsonObject = JSONObject.parseObject(response.toString(), JSONObject.class);
                                JSONArray tips = jsonObject.getJSONArray("tips");
                                if (tips.size() > 0) {
                                    for (Object tip : tips) {
                                        JSONObject jsonTip = (JSONObject) tip;
                                        String name = jsonTip.getString("name");
                                        String district = jsonTip.getString("district");
                                        String address = jsonTip.getString("address");
                                        String poiId = jsonTip.getString("id");
                                        tipsList.add(new Tip(name, poiId, district + address));
                                    }
                                } else {
                                    tipsList.add(new Tip("空", "", ""));
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    reader.close();
                                    inputStream.close();
                                    inputStreamReader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
//                            System.out.println("子线程执行结束");
                        }
                    });
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //显示到控件上
//                    MaxHeightRecyclerView end_point_tipsListView = end_point_tips_list.findViewById(R.id.tips_list);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                    end_point_tipsListView.setLayoutManager(layoutManager);
                    InputTipsAdapter inputTipsAdapter = new InputTipsAdapter(tipsList);
                    end_point_tipsListView.setAdapter(inputTipsAdapter);
                    end_point_tips_list.setVisibility(View.VISIBLE);
                } else {
                    end_point_tips_list.setVisibility(View.INVISIBLE);
                }
            }
        });
        new_place.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //调用输入提示API，将结果赋给List<Tip>对象
                String text = new_place.getText().toString().trim();
                //搜索框中有内容
                if (null != text && !(text.equals("")) && new_place.isFocused()) {
                    //输入提示API调用
                    String uri = "https://restapi.amap.com/v3/assistant/inputtips?" +
                            "key=" + "6d61a5918c86aaaeb6802c14133a8519" + "&" +
                            "keywords=" + text + "&" +
                            "output=" + "json" + "&" +
                            "datatype=" + "all";
                    List tipsList = new ArrayList<Tip>();

                    //使用HttPURLConnection进行网络请求
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            System.out.println("执行子线程");
                            HttpURLConnection connection = null;
                            BufferedReader reader = null;
                            InputStream inputStream = null;
                            InputStreamReader inputStreamReader = null;
                            try {
                                URL url = new URL(uri);
                                connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");
                                connection.setConnectTimeout(8000);
                                connection.setReadTimeout(8000);
                                inputStream = connection.getInputStream();
                                //下面对获取到的输入流进行读取
                                inputStreamReader = new InputStreamReader(inputStream);

                                reader = new BufferedReader(inputStreamReader);
                                StringBuilder response = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    response.append(line);
                                }

                                //将json字符串解析出来
                                JSONObject jsonObject = JSONObject.parseObject(response.toString(), JSONObject.class);
                                JSONArray tips = jsonObject.getJSONArray("tips");
                                if (tips.size() > 0) {
                                    for (Object tip : tips) {
                                        JSONObject jsonTip = (JSONObject) tip;
                                        String name = jsonTip.getString("name");
//                                        System.out.println(name);
                                        String district = jsonTip.getString("district");
                                        String address = jsonTip.getString("address");
                                        String poiId = jsonTip.getString("id");
                                        tipsList.add(new Tip(name, poiId, district + address));
                                    }
                                } else {
                                    tipsList.add(new Tip("空", "", ""));
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    reader.close();
                                    inputStream.close();
                                    inputStreamReader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
//                            System.out.println("子线程执行结束");
                        }
                    });
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //将数据显示到控件上
//                    MaxHeightRecyclerView new_place_tipsListView = new_place_tips_list.findViewById(R.id.tips_list);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                    new_place_tipsListView.setLayoutManager(layoutManager);
                    InputTipsAdapter inputTipsAdapter = new InputTipsAdapter(tipsList);
                    new_place_tipsListView.setAdapter(inputTipsAdapter);
                    new_place_tips_list.setVisibility(View.VISIBLE);
                } else {
                    new_place_tips_list.setVisibility(View.INVISIBLE);
                }
            }
        });
        //以下都用于改变editText的焦点状态，以收起输入提示栏
        starting_point.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 获得焦点
                } else {
                    // 失去焦点
                    starting_point_tips_list.setVisibility(View.INVISIBLE);
                }
            }
        });
        end_point.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 获得焦点
                } else {
                    // 失去焦点
                    end_point_tips_list.setVisibility(View.INVISIBLE);
                }
            }
        });
        new_place.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 获得焦点
                } else {
                    // 失去焦点
                    new_place_tips_list.setVisibility(View.INVISIBLE);
                }
            }
        });
        primary_search_box.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                primary_search_box.setFocusable(true);
                primary_search_box.setFocusableInTouchMode(true);
                primary_search_box.requestFocus();

                return false;
            }
        });
        advanced_dialog.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                advanced_dialog.setFocusable(true);
                advanced_dialog.setFocusableInTouchMode(true);
                advanced_dialog.requestFocus();

                return false;
            }
        });
        //测试功能监听
        //①定位
//        btn_test_locate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openGPS();
//            }
//        });
//        //②导航
//        btn_test_nav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nav();
//            }
//        });
//        //③poi搜索
//        btn_test_poi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String address = starting_point.getText().toString().trim();
////                System.out.println(address);
//                getPOI(address);
//            }
//        });
    }

    //使用Web服务接口，查询POI信息：根据名称搜索POI（返回：名称、经纬度、PI）
    public Map getPOI(String address) {
//        System.out.println("搜索" + address);
//        AsyncHttpClient client = new AsyncHttpClient();
        String uri = "https://restapi.amap.com/v3/place/text?" +
                "key=" + "6d61a5918c86aaaeb6802c14133a8519" + "&" +
                "keywords=" + address + "&" +
                "children=" + "0" + "&" +
                "offset=" + "1" + "&" +
                "page=" + "1" + "&" +
                "extensions=" + "all" + "&" +
                "output=" + "json" + "&" +
                "datatype=" + "poi";
//        System.out.println(uri);

        //使用OkHttp的方式网络请求

//        OkHttpClient httpClient = new OkHttpClient();
//        Request request = new Request.Builder().url(uri).build();
//
//        //android3.0版本开始就强制程序不能在主线程中访问网络，要把访问网络放在独立的线程中
//        thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("执行子线程");
//                try {
//                    response = httpClient.newCall(request).execute();
//                    System.out.println(response);
//                    System.out.println("进行网络请求");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();
//
//        try {
//            //等待子线程执行结束
//            thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        String responseData = response.body().string();
//        System.out.println(responseData);
//
//        JSONObject jsonObject = JSONObject.parseObject(responseData, JSONObject.class);
//        if (jsonObject != null) {
//            System.out.println("请求成功！");
//            Toast.makeText(MainActivity.this, "请求成功", Toast.LENGTH_SHORT).show();
//        }
//
//        JSONArray pois = jsonObject.getJSONArray("pois");
//
//        JSONObject poi = pois.getJSONObject(0);
//        String name = poi.getString("name");
//        String location = poi.getString("location");
//        String poiid = poi.getString("id");
////                    System.out.println("name : " + name + " , " + "location : " + location + " , " + "poiid : " + poiid);
//
//        resultMap = new HashMap<String, String>();
//        resultMap.put("name", name);
//        resultMap.put("location", location);
//        resultMap.put("poiid", poiid);

        //使用AsyncHttpClient进行网络请求
//        client.get(uri, new AsyncHttpResponseHandler() {
//            //请求成功
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                System.out.println("请求成功！");
//                Toast.makeText(MainActivity.this, "请求成功", Toast.LENGTH_SHORT).show();
//                try {
//                    String responseJsonStr = new String(responseBody, "utf-8");
//
//                    JSONObject jsonObject = JSONObject.parseObject(responseJsonStr, JSONObject.class);
//                    JSONArray pois = jsonObject.getJSONArray("pois");
//                    JSONObject poi = pois.getJSONObject(0);
//
//                    String name = poi.getString("name");
//                    String location = poi.getString("location");
//                    String poiid = poi.getString("id");
//
//                    System.out.println("name : " + name + " , " + "location : " + location + " , " + "poiid : " + poiid);
//
//                    resultMap = new HashMap<String, String>();
//                    resultMap.put("name", name);
//                    resultMap.put("location", location);
//                    resultMap.put("poiid", poiid);
//                    String[] strings = location.split(",");
//                    //经度
//                    resultMap.put("longitude", strings[0]);
//                    //纬度
//                    resultMap.put("latitude", strings[1]);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            //请求失败
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
//                    error) {
//                System.out.println("请求失败！");
//                System.out.println(error);
//                System.out.println(statusCode);
//                System.out.println(responseBody.toString());
//                Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
//            }
//        });

        //使用HttPURLConnection进行网络请求
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
//                System.out.println("执行子线程");
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                InputStream inputStream = null;
                InputStreamReader inputStreamReader = null;
                try {
                    URL url = new URL(uri);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    inputStream = connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    inputStreamReader = new InputStreamReader(inputStream);

                    reader = new BufferedReader(inputStreamReader);
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

//                    System.out.println("response.toString()：" + response.toString());
                    //将json字符串解析出来
                    JSONObject jsonObject = JSONObject.parseObject(response.toString(), JSONObject.class);
//                    System.out.println("断点" + jsonObject.toString());
                    JSONArray pois = jsonObject.getJSONArray("pois");
//                    System.out.println("pois：" + pois.toString());
                    JSONObject poi = pois.getJSONObject(0);

                    String name = poi.getString("name");
                    String location = poi.getString("location");
                    String poiid = poi.getString("id");

//                    System.out.println("name : " + name + " , " + "location : " + location + " , " + "poiid : " + poiid);

                    resultMap = new HashMap<String, String>();
                    resultMap.put("name", name);
                    resultMap.put("location", location);
                    resultMap.put("poiid", poiid);
                    String[] strings = location.split(",");
                    //经度
                    resultMap.put("longitude", strings[0]);
                    //纬度
                    resultMap.put("latitude", strings[1]);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        reader.close();
                        inputStream.close();
                        inputStreamReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                System.out.println("子线程执行结束");
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("继续执行主线程");

        return resultMap;
    }

    //使用android接口，查询POI信息
    public void getPOIDirectly(String address) throws com.amap.api.services.core.AMapException {
//        System.out.println("开始POI搜索");
        PoiSearch.Query query = new PoiSearch.Query(address, "", "");
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(1);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);//设置查询页码
        //南京信息工程大学 南京南站
        //构造 PoiSearch 对象，并设置监听。
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);

        //调用 PoiSearch 的 searchPOIAsyn() 方法发送请求。
        //异步方式获取POI
        poiSearch.searchPOIAsyn();
        //同步方式获取POI
//        PoiResult poiResult = poiSearch.searchPOI();


//        PoiItem poiItem = poiResult.getPois().get(0);

//        poi.put("name", poiItem.getAdName());
//        poi.put("poiid", poiItem.getPoiId());
//        poi.put("latitude", String.valueOf(poiItem.getLatLonPoint().getLatitude()));
//        poi.put("longitude", String.valueOf(poiItem.getLatLonPoint().getLongitude()));
//        System.out.println("POI搜索结束");
    }

    //开启本机定位，获取定位数据（设置为仅进行一次定位）
    public void openGPS() {
        //初始化定位
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);
        try {
            mLocationListener = new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
//                    System.out.println("地址发生变化");
                    if (aMapLocation != null) {
//                        MainActivity.myLocalCity = aMapLocation.getCity();//城市信息
//                        System.out.println(aMapLocation.getErrorCode());
                        if (aMapLocation.getErrorCode() == 0) {
                            //可在其中解析amapLocation获取相应内容。
                            int locationType = aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                            System.out.println(locationType);
                            double latitude = aMapLocation.getLatitude();//获取纬度
//                            System.out.println(latitude);
                            double longitude = aMapLocation.getLongitude();//获取经度
//                            System.out.println(longitude);
                            float accuracy = aMapLocation.getAccuracy();//获取精度信息
//                            System.out.println(accuracy);
                            String address = aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                            System.out.println(address);
                            String country = aMapLocation.getCountry();//国家信息
//                            System.out.println(country);
                            String province = aMapLocation.getProvince();//省信息
//                            System.out.println(province);
                            String city = aMapLocation.getCity();//城市信息
//                            System.out.println(city);
                            String district = aMapLocation.getDistrict();//城区信息
//                            System.out.println(district);
                            String street = aMapLocation.getStreet();//街道信息
//                            System.out.println(street);
                            String streetNum = aMapLocation.getStreetNum();//街道门牌号信息
//                            System.out.println(streetNum);
                            String cityCode = aMapLocation.getCityCode();//城市编码
//                            System.out.println(cityCode);
                            String adCode = aMapLocation.getAdCode();//地区编码
//                            System.out.println(adCode);
                            String aoiName = aMapLocation.getAoiName();//获取当前定位点的AOI信息
//                            System.out.println(aoiName);
                            String buildingId = aMapLocation.getBuildingId();//获取当前室内定位的建筑物Id
//                            System.out.println(buildingId);
                            String floor = aMapLocation.getFloor();//获取当前室内定位的楼层
//                            System.out.println(floor);
                            int gpsAccuracyStatus = aMapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
//                            System.out.println(gpsAccuracyStatus);
                            //获取定位时间
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date(aMapLocation.getTime());
                            String format = df.format(date);

//                            System.out.println(date);
//                            System.out.println(format);
                            Toast.makeText(MainActivity.this, "已开启定位", Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "经度：" + longitude + "，纬度：" + latitude, Toast.LENGTH_SHORT).show();
                        } else {
                            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                            Log.e("AmapError", "location Error, ErrCode:"
                                    + aMapLocation.getErrorCode() + ", errInfo:"
                                    + aMapLocation.getErrorInfo());
                            Toast.makeText(MainActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };
            mLocationClient = new AMapLocationClient(getApplicationContext());
            //设置定位回调监听
            if (null != mLocationClient) {
                mLocationClient.setLocationListener(mLocationListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }

        //仅进行一次定位
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        //mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);

        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(3000);

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);

        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);

        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);

        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);

        if (null != mLocationClient) {
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //启动定位
            mLocationClient.startLocation();
        }
    }

    //停止定位
    public void stopGPS() {
        if (null != mLocationClient) {
            mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
            Toast.makeText(this, "停止定位", Toast.LENGTH_SHORT).show();
        }
    }

    //销毁定位客户端
    public void destoryGPSClient() {
        if (null != mLocationClient) {
            mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
        }
    }

    //导航（至多途径3个地点）
    public void nav() {

        //在调用AMapNavi.getInstance 之前必须进行合规检查，设置接口之前保证隐私政策合规，检查接口如下
        NaviSetting.updatePrivacyShow(this, true, true);
        NaviSetting.updatePrivacyAgree(this, true);

        //构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
//        AmapNaviParams params = new AmapNaviParams(null, null, null, AmapNaviType.DRIVER, AmapPageType.ROUTE);
        //启动导航组件
//        AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);

        //起点
        Poi start = new Poi("北京首都机场", new LatLng(40.080525, 116.603039), "B000A28DAE");
        //途经点
        List<Poi> poiList = new ArrayList();
        poiList.add(new Poi("故宫", new LatLng(39.918058, 116.397026), "B000A8UIN8"));
        //终点
        Poi end = new Poi("北京大学", new LatLng(39.941823, 116.426319), "B000A816R6");
        // 组件参数配置（最后一个参数，AmapPageType.NAVI为导航界面，AmapPageType.ROUTE为路线规划界面）
        AmapNaviParams params = new AmapNaviParams(start, poiList, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);

        //可以实现该接口后,将回调实例通过启动组件方法的最后一个参数传入
        INaviInfoCallback iNaviInfoCallback = new INaviInfoCallback() {
            @Override
            public void onInitNaviFailure() {

            }

            /**
             * 导航播报信息回调函数。
             *
             * @param s 播报文字。
             * @since 5.2.0
             */
            public void onGetNavigationText(String s) {
            }

            /**
             * 当GPS位置有更新时的回调函数。
             *
             * @param location 当前位置的定位信息。
             * @since 5.2.0
             */
            public void onLocationChange(AMapNaviLocation location) {
            }

            @Override
            public void onArriveDestination(boolean b) {

            }

            @Override
            public void onStartNavi(int i) {

            }

            @Override
            public void onCalculateRouteSuccess(int[] ints) {

            }

            @Override
            public void onCalculateRouteFailure(int i) {

            }

            @Override
            public void onStopSpeaking() {

            }

            @Override
            public void onReCalculateRoute(int i) {

            }

            /**
             * 退出组件或退出组件导航的回调函数
             * @param pageType 参见{@link PageType}
             * @since 5.6.0
             */
            public void onExitPage(int pageType) {
            }

            /**
             * 策略选择界面中切换算路偏好回调
             * @param strategy 切换后偏好 参见{@link PathPlanningStrategy}
             * @since 6.0.0
             */
            public void onStrategyChanged(int strategy) {
            }

            @Override
            public void onArrivedWayPoint(int i) {

            }

            /**
             * 获取导航地图自定义View，该View在导航整体界面的下面，注意要设置setLayoutParams并且设置高度
             * 设置底部自定义View高度时要给导航组件留出足够的空间用来展示导航控件，避免导航控件出现挤压或重叠的问题
             * @return View
             * @since 6.1.0
             */
            public View getCustomNaviBottomView() {
                return null;
            }

            /**
             * 获取导航地图自定义View,该View在导航界面的当前路名位置，使用该方法以后，将不会显示当前路名
             * @return View
             * @since 6.1.0
             */
            public View getCustomNaviView() {
                return null;
            }

            /**
             * 组件地图白天黑夜模式切换回调
             * @param mapType 枚举值参考AMap类, 3-黑夜，4-白天
             * @since 6.7.0
             */
            public void onMapTypeChanged(int mapType) {
            }

            /**
             * 获取导航地图自定义View,该View在导航界面的垂直居中，水平靠左位置
             * @return View
             * @since 6.9.0
             */
            public View getCustomMiddleView() {
                return null;
            }

            /**
             * 导航视角变化回调
             * @since 7.1.0
             * @param naviMode 导航视角, 1-正北朝上模式 2-车头朝上状态
             */
            public void onNaviDirectionChanged(int naviMode) {
            }

            /**
             * 昼夜模式设置变化回调
             * @since 7.1.0
             * @param mode 0-自动切换 1-白天 2-夜间
             */
            public void onDayAndNightModeChanged(int mode) {
            }

            /**
             * 播报模式变化回调
             * @since 7.1.0
             * @param mode 1-简洁播报 2-详细播报 3-静音
             */
            public void onBroadcastModeChanged(int mode) {
            }

            /**
             * 比例尺智能缩放设置变化回调
             * @since 7.1.0
             * @param enable 是否开启
             */
            public void onScaleAutoChanged(boolean enable) {
            }
        };

        // 启动组件（最后一个参数为上面的回调实例）
        AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);
        //还有一些高级配置功能，AmapNaviParams中提供了很多配置方法，支持在启动同时传入，满足自定义需求。（详情参见导航组件的高级功能）


    }

    //退出导航
    public void exitNav() {
        //退出导航组件
        AmapNaviPage.getInstance().exitRouteActivity();
    }

    //根据POI信息计算最佳路程线路
    public void bestLengthRoute(Place placeA, Place placeB) {
//        System.out.println(placeA);
//        System.out.println(placeB);
        //在调用AMapNavi.getInstance 之前必须进行合规检查，设置接口之前保证隐私政策合规，检查接口如下
        NaviSetting.updatePrivacyShow(this, true, true);
        NaviSetting.updatePrivacyAgree(this, true);

        // 起点信息
        NaviPoi start = new NaviPoi(placeA.getName(), null, placeA.getPoiid());
        // 终点信息
        NaviPoi end = new NaviPoi(placeB.getName(), null, placeB.getPoiid());
        // POI算路
        mAMapNavi.calculateDriveRoute(start, end, null, PathPlanningStrategy.DRIVING_SHORTEST_DISTANCE);
        //POI算路中，NaviPoi对象内也可以传入经纬度信息，优先使用POIID，如果POIID无效，则使用经纬度算路
        //当路线规划成功时，会触发 AMapNaviListener 的 onCalculateRouteSuccess 回调，在该回调函数中，可以获取路线对象，进行规划路线的显示
    }

    //根据POI信息计算最佳时间线路
    public void bestTimeRoute(Place placeA, Place placeB) {
//        System.out.println("在画 " + placeA.getName() + " 和 " + placeB.getName() + " 之间的距离");
        //在调用AMapNavi.getInstance 之前必须进行合规检查，设置接口之前保证隐私政策合规，检查接口如下
        NaviSetting.updatePrivacyShow(this, true, true);
        NaviSetting.updatePrivacyAgree(this, true);

        // 起点信息
        NaviPoi start = new NaviPoi(placeA.getName(), null, placeA.getPoiid());
        Poi start2 = new Poi(placeA.getName(), null, placeA.getPoiid());
        // 终点信息
        NaviPoi end = new NaviPoi(placeB.getName(), null, placeB.getPoiid());
        Poi end2 = new Poi(placeB.getName(), null, placeB.getPoiid());
        // POI算路
        mAMapNavi.calculateDriveRoute(start, end, null, PathPlanningStrategy.DRIVING_DEFAULT);
        //开启导航
//        AmapNaviParams params = new AmapNaviParams(start2, null, end2, AmapNaviType.DRIVER, AmapPageType.ROUTE);
//        AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);
        //POI算路中，NaviPoi对象内也可以传入经纬度信息，优先使用POIID，如果POIID无效，则使用经纬度算路
        //当路线规划成功时，会触发 AMapNaviListener 的 onCalculateRouteSuccess 回调，在该回调函数中，可以获取路线对象，进行规划路线的显示
    }

    //根据排好的顺序，规划路线
    public void multPointBestRoute(/*起点信息*/NaviPoi start, /*终点信息*/NaviPoi end, List<NaviPoi> waysPoiIds) {
        //在调用AMapNavi.getInstance 之前必须进行合规检查，设置接口之前保证隐私政策合规，检查接口如下
        NaviSetting.updatePrivacyShow(this, true, true);
        NaviSetting.updatePrivacyAgree(this, true);
        // POI算路
//        System.out.print(start.getName() + "  " + start.getPoiId() + "   ------>   ");
//        for (NaviPoi waysPoiId : waysPoiIds) {
//            System.out.print(waysPoiId.getName() + "  " + waysPoiId.getPoiId() + "   ------>   ");
//        }
//        System.out.println(end.getName() + "     " + end.getPoiId());

        if (radio_strategy_advanced.getCheckedRadioButtonId() == radio_shortestTime_advanced.getId()) {
            mAMapNavi.calculateDriveRoute(start, end, waysPoiIds, PathPlanningStrategy.DRIVING_DEFAULT);
//            mAMapNavi.calculateDriveRoute(start, end, naviPois, PathPlanningStrategy.DRIVING_DEFAULT);
        } else {
            mAMapNavi.calculateDriveRoute(start, end, waysPoiIds, PathPlanningStrategy.DRIVING_SHORTEST_DISTANCE);
        }
        //POI算路中，NaviPoi对象内也可以传入经纬度信息，优先使用POIID，如果POIID无效，则使用经纬度算路
        //当路线规划成功时，会触发 AMapNaviListener 的 onCalculateRouteSuccess 回调，在该回调函数中，可以获取路线对象，进行规划路线的显示
    }

    //初始化地图
    void mapInit(Bundle savedInstanceState) {
        MapsInitializer.updatePrivacyShow(this, true, true);
        MapsInitializer.updatePrivacyAgree(this, true);

        MapView mapView = (MapView) viewItem.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();

        //获取AMapNavi
        try {
            mAMapNavi = AMapNavi.getInstance(this);
        } catch (
                AMapException e) {
            e.printStackTrace();
        }

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。

        //定位蓝点八种模式
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);//连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
        //以下三种模式从5.1.0版本开始提供
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。

        myLocationStyle.interval(120000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        //精度圆圈自定义

        //方法自5.1.0版本后支持
        myLocationStyle.showMyLocation(true);//设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。
        //精度圆圈的自定义
        myLocationStyle.strokeColor(Color.TRANSPARENT);//设置定位蓝点精度圆圈的边框颜色的方法
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);//设置定位蓝点精度圆圈的填充颜色的方法
        myLocationStyle.strokeWidth(0);//设置定位蓝点精度圆圈的边框宽度的方法

        //定位蓝点图标自定义
//        MyLocationStyle myLocationIcon(BitmapDescriptor myLocationIcon);//设置定位蓝点的icon图标方法，需要用到BitmapDescriptor类对象作为参数。

        aMap.addOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                //经度
//                double longitude = location.getLongitude();
//                System.out.println("经度：" + longitude);
                //纬度
//                double altitude = location.getAltitude();
//                System.out.println("纬度：" + altitude);
            }
        });

        //添加地图小控件
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setZoomControlsEnabled(true);//缩放按钮
        mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        mUiSettings.setCompassEnabled(true);//指南针

//        //定位按钮
//        aMap.setLocationSource(this);//通过aMap对象设置定位数据源的监听
        mUiSettings.setMyLocationButtonEnabled(true); //显示默认的定位按钮
//        aMap.setMyLocationEnabled(true);// 可触发定位并显示当前位置
//
        mUiSettings.setScaleControlsEnabled(true);//控制比例尺控件是否显示

        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);//设置logo位置
        /*
            AMapOptions.LOGO_POSITION_BOTTOM_LEFT       LOGO边缘MARGIN（左边）
            AMapOptions.LOGO_MARGIN_BOTTOM              LOGO边缘MARGIN（底部
            AMapOptions.LOGO_MARGIN_RIGHT               LOGO边缘MARGIN（右边）
            AMapOptions.LOGO_POSITION_BOTTOM_CENTER     Logo位置（地图底部居中）
            AMapOptions.LOGO_POSITION_BOTTOM_LEFT       Logo位置（地图左下角）
            AMapOptions.LOGO_POSITION_BOTTOM_RIGHT      Logo位置（地图右下角）
         */
    }

    //逆地理编码
    public void reverse() {
        String uri = "https://restapi.amap.com/v3/geocode/regeo?";
        AsyncHttpClient client = new AsyncHttpClient();
        AsyncHttpResponseHandler asyncHttpResponseHandler = new AsyncHttpResponseHandler() {
            //请求成功
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                System.out.println("请求成功！");
                Toast.makeText(MainActivity.this, "请求成功", Toast.LENGTH_SHORT).show();
                try {
                    String responseJsonStr = new String(responseBody, "utf-8");

//                    System.out.println(responseJsonStr);

                    JSONObject jsonObject = JSONObject.parseObject(responseJsonStr, JSONObject.class);

                    JSONArray pois = jsonObject.getJSONArray("pois");

                    JSONObject poi = pois.getJSONObject(0);
                    String name = poi.getString("name");
                    String location = poi.getString("location");
                    String poiid = poi.getString("id");
                    System.out.println("name : " + name + " , " + "location : " + location + " , " + "poiid : " + poiid);

                    resultMap = new HashMap<String, String>();
                    resultMap.put("name", name);
                    resultMap.put("location", location);
                    resultMap.put("poiid", poiid);
                    String[] strings = location.split(",");
                    //经度
                    resultMap.put("longitude", strings[0]);
                    //纬度
                    resultMap.put("latitude", strings[1]);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            //请求失败
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                System.out.println("请求失败！");
                Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }
        };
        client.get(uri, asyncHttpResponseHandler);
    }

    //权限检查与申请
    public void permissionRequest() {
        //这里以ACCESS_COARSE_LOCATION为例
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);//自定义的code
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2);//自定义的code
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    3);//自定义的code
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    4);//自定义的code
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    5);//自定义的code
        }
    }

    //接收权限请求的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //权限赋予成功
//        System.out.println("请求码为" + requestCode + "权限" + permissions + "赋予成功");
    }

    //用异步方式获取POI信息成功之后，会回调这个函数
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        //南京信息工程大学 南京南站
//        System.out.println("搜索到多条记录");
        ArrayList<PoiItem> pois = poiResult.getPois();
        PoiItem poiItem = pois.get(0);
        LatLonPoint latLonPoint = poiItem.getLatLonPoint();

        if (null == poi) {
            poi = new HashMap<String, String>();
        }

        poi.put("name", poiItem.getAdName());
        poi.put("poiid", poiItem.getPoiId());
        poi.put("latitude", String.valueOf(latLonPoint.getLatitude()));
        poi.put("longitude", String.valueOf(latLonPoint.getLongitude()));

        //flag为true，代表此时全局变量poi中含有正确的信息
        flag = true;

        //通知
//        countDownLatch.countDown();
//        System.out.println("CountDownLatch计数减一");
//        System.out.println("name：" + poiItem.getAdName());
//        System.out.println("poiid：" + poiItem.getPoiId());
//        System.out.println("latitude：" + String.valueOf(latLonPoint.getLatitude()));
//        System.out.println("longitude：" + String.valueOf(latLonPoint.getLongitude()));
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
//        System.out.println("搜索到单条记录");
//        LatLonPoint latLonPoint = poiItem.getLatLonPoint();
//        System.out.println("name：" + poiItem.getAdName());
//        System.out.println("poiid：" + poiItem.getPoiId());
//        System.out.println("location：" + poiItem.getLatLonPoint());
//        System.out.println("latitude：" + latLonPoint.getLatitude());
//        System.out.println("longitude：" + latLonPoint.getLongitude());
    }

    //Web服务API：获取两地之间行驶的距离：需要用到两个地点参数的经度、和纬度（这两个属性不能为空）
    public Map getDistance(Place placeA, Place placeB, int strategy) {
        String uri = "https://restapi.amap.com/v3/direction/driving?" +
                "key=" + "6d61a5918c86aaaeb6802c14133a8519" + "&" +
                "origin=" + placeA.getLongitude() + "," + placeA.getLatitude() + "&" +
                "destination=" + placeB.getLongitude() + "," + placeB.getLatitude() + "&" +
                "strategy=" + strategy + "&" +
                "output=" + "json" + "&"
                + "extensions=" + "base";

//        System.out.println(uri);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
//                System.out.println("执行子线程");
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(uri);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputStream = connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

//                    System.out.println(response.toString());

                    //将json字符串解析出来
                    JSONObject jsonObject = JSONObject.parseObject(response.toString(), JSONObject.class);
                    JSONObject route = jsonObject.getJSONObject("route");
                    JSONArray paths = route.getJSONArray("paths");
                    JSONObject firstPath = paths.getJSONObject(0);
                    String distance = firstPath.getString("distance");
                    String duration = firstPath.getString("duration");

//                    System.out.println("最短路程策略，" + "路程：" + distance + "，耗时：" + duration);

                    resultMap = new HashMap<String, String>();
                    resultMap.put("distance", distance);
                    resultMap.put("duration", duration);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                System.out.println("子线程执行结束");
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        System.out.println("继续执行主线程");

//        if (null != resultMap) {
//            System.out.println("获取路程和时长：");
//            System.out.println(resultMap);
//            System.out.println(resultMap.toString());
//        }
        return resultMap;
    }

    //Web服务API：获取两地之间行驶的时间：需要用到两个地点参数的经度、和纬度（这两个属性不能为空）
    public Map getDuration(Place placeA, Place placeB, int strategy) {
        String uri = "https://restapi.amap.com/v3/direction/driving?" +
                "key=" + "6d61a5918c86aaaeb6802c14133a8519" + "&" +
                "origin=" + placeA.getLongitude() + "," + placeA.getLatitude() + "&" +
                "destination=" + placeB.getLongitude() + "," + placeB.getLatitude() + "&" +
                "strategy=" + strategy + "&" +
                "output=" + "json" + "&"
                + "extensions=" + "base";

//        System.out.println(uri);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
//                System.out.println("执行子线程");
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(uri);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputStream = connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    System.out.println(response.toString());

                    //将json字符串解析出来
                    JSONObject jsonObject = JSONObject.parseObject(response.toString(), JSONObject.class);
                    JSONObject route = jsonObject.getJSONObject("route");
                    JSONArray paths = route.getJSONArray("paths");
                    JSONObject firstPath = paths.getJSONObject(0);
                    String distance = firstPath.getString("distance");
                    String duration = firstPath.getString("duration");

                    System.out.println("最短耗时策略，" + "路程：" + distance + "，耗时：" + duration);

                    resultMap = new HashMap<String, String>();
                    resultMap.put("distance", distance);
                    resultMap.put("duration", duration);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                System.out.println("子线程执行结束");
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        System.out.println("继续执行主线程");

        return resultMap;
    }

    //多点规划时，需要获取两两点之间的数据，以一个二维数组形式返回数据
    public int[][] getDataTable(List list, int strategy) {
        int[][] dataTable = new int[list.size()][list.size()];
        Place a = null;
        Place b = null;

        //先算出上三角的数据
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                Map poi1 = getPOI((String) list.get(i));
                Map poi2 = getPOI((String) list.get(j));
                a = new Place(null, null, null, (String) poi1.get("longitude"), (String) poi1.get("latitude"));
                b = new Place(null, null, null, (String) poi2.get("longitude"), (String) poi2.get("latitude"));
                switch (strategy) {
                    //最短路程策略
                    case PathPlanningStrategy.DRIVING_SHORTEST_DISTANCE:
//                        System.out.println("计算最短路程");
                        Map distance = getDistance(a, b, PathPlanningStrategy.DRIVING_DEFAULT);
//                        System.out.println(resultMap);
                        if (null != resultMap) {
                            System.out.println("验证返回的路程和时长是否为null：");
                            System.out.println(resultMap);
                            System.out.println(resultMap.toString());
                        }
                        /*不要使用Integer.getInteger(String)方法*/
                        dataTable[i][j] = Integer.valueOf((String) distance.get("distance"));
                        break;
                    //最短耗时策略
                    case PathPlanningStrategy.DRIVING_DEFAULT:
//                        System.out.println("计算最短时间");
                        Map duration = getDuration(a, b, PathPlanningStrategy.DRIVING_DEFAULT);
//                        System.out.println(resultMap);
                        if (null != resultMap) {
//                            System.out.println("验证返回的路程和时长是否为null：");
//                            System.out.println(resultMap);
//                            System.out.println(resultMap.toString());
                        }
                        dataTable[i][j] = Integer.valueOf((String) duration.get("duration"));
                        break;
                }
            }
        }

        //由于是对称矩阵，所以直接可由上三角获得下三角数据
        for (int j = 0; j < list.size(); j++) {
            for (int i = j + 1; i < list.size(); i++) {
                dataTable[i][j] = dataTable[j][i];
            }
        }
//        System.out.println("数据矩阵：");
//        System.out.println(Arrays.deepToString(dataTable));
//        for (int i = 0; i < dataTable.length; i++) {
//            System.out.print("[");
//            for (int j = 0; j < dataTable[i].length - 1; j++) {
//                if (i == j) {
//                    System.out.print(dataTable[i][j] + ", \t\t");
//                } else {
//                    System.out.print(dataTable[i][j] + ", \t");
//                }
//            }
//            if (i == dataTable[i].length - 1) {
//                System.out.print(dataTable[i][dataTable[i].length - 1] + ", \t\t");
//            } else {
//                System.out.println(dataTable[i][dataTable[i].length - 1] + "]");
//            }
//        }
        return dataTable;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
//        System.out.println("激活定位源。");
    }

    @Override
    public void deactivate() {
//        System.out.println("停止定位。");
    }
}