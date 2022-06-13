package com.example.perfectnavigationassistant.activity;

import static cn.bmob.v3.Bmob.getApplicationContext;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.perfectnavigationassistant.R;
import com.example.perfectnavigationassistant.adapter.AccommodationAdapter;
import com.example.perfectnavigationassistant.adapter.FilterAdapter;
import com.example.perfectnavigationassistant.adapter.FoodAdapter;
import com.example.perfectnavigationassistant.adapter.PlaceAdapter;
import com.example.perfectnavigationassistant.bean.Accommodation;
import com.example.perfectnavigationassistant.bean.Filter;
import com.example.perfectnavigationassistant.bean.Food;
import com.example.perfectnavigationassistant.bean.Place;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

//public class RecommendationActivity extends AppCompatActivity {
public class RecommendationActivity {
    View viewItem = null;
    Context context = null;
    //================================
    //控件
    private SearchView searchView;
    private ListView list;
    private Button food;
    private Button place;
    private Button accommodation;
    private LinearLayout condition;
    private RecyclerView indexRecyclerView;
    private TextView indexAll;
    private TextView indexHotpot;
    private TextView indexMeat;
    private TextView indexForeign;
    private TextView indexRegion;
    private TextView indexOther;
    //    private RecyclerView searchFoodRecyclerView;
    //    private RecyclerView searchPlaceRecyclerView;
    //    private RecyclerView searchAcRecyclerView;
    //    private LinearLayout searchFrame;
    private RecyclerView searchFoodRecyclerView;
    private RecyclerView searchPlaceRecyclerView;
    private RecyclerView searchAcRecyclerView;
    private LinearLayout foodFrame;
    private LinearLayout placeFrame;
    private LinearLayout acFrame;
    private Button typeFood;
    private Button typePlace;
    private Button typeAc;
    private LinearLayout searchType;
    private Spinner presentLocation;
    private ArrayAdapter<String> locationAdapter;
    private ArrayAdapter<Filter> listAdapter;
    private FoodAdapter indexAdapter;
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//
//        }
//    };

    //数据
    //private String[] strings = new String[]{"曼谷街头", "披萨", "小菜园", "红山动物园", "总统府", "汉庭酒店"};

    private List<Filter> filterList = new ArrayList<>();
    private List<Food> foodList = new ArrayList<Food>();
    private List<Place> placeList = new ArrayList<Place>();
    private List<Accommodation> acList = new ArrayList<Accommodation>();
    private String[] locationData;
    private String presentCity = "北京";


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_recommendation);
//        Bmob.initialize(this, "be29b9fb7187aa9f260f86cc427473ed");
//
////        System.out.println("初始化推荐数据");
//        //初始化推荐数据
//        initFoods();
//        initPlaces();
//        initAccommodations();
//
//        recommendClick();
//        chooseCity();
//        search();
//
//    }

    public void start(Context context, View parentView) {
        this.context = context;
        viewItem = parentView;

        //初始化推荐数据
        initFoods();
        initPlaces();
        initAccommodations();

        tagClick();
        recommendClick();
        chooseCity();
        search();
    }

    private void initFoods() {
        //初始化美食推荐数据
//        System.out.println("初始化美食推荐数据");
        BmobQuery<Food> foodBmobQuery = new BmobQuery<Food>();
        foodBmobQuery.addWhereEqualTo("city", presentCity);
        foodBmobQuery.findObjects(new FindListener<Food>() {
            @Override
            public void done(List<Food> list, BmobException e) {
//                System.out.println("list大小" + list.size());
                if (e == null) {
                    for (Food foodItem : list) {
                        foodList.add(foodItem);
                        //Log.d("foodImg", "foodImg: " + foodItem.getFoodImg());
                    }
                    //初始推荐列表
                    indexRecyclerView = viewItem.findViewById(R.id.recommend_item);
                    StaggeredGridLayoutManager foodLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    indexRecyclerView.setLayoutManager(foodLayoutManager);
                    indexAdapter = new FoodAdapter(foodList, RecommendationActivity.this.context);
                    indexRecyclerView.setAdapter(indexAdapter);
//                    System.out.println("打印foodList，大小：" + foodList.size());
//                    System.out.println("foodList查询成功" + foodList.size());
                } else {
//                    System.out.println("foodList查询失败");
                }
            }
        });
    }

    private void search() {
        //给搜索栏设置点击事件
        searchView = viewItem.findViewById(R.id.search);
        condition = viewItem.findViewById(R.id.condition);
        list = viewItem.findViewById(R.id.list);
        initFilters();
        //设置ListView启动过滤
        list.setTextFilterEnabled(true);
        //设置该SearchView默认是否自动缩小为图标
        searchView.setIconifiedByDefault(false);
        //设置该SearchView显示搜索图标
        searchView.setSubmitButtonEnabled(true);
        //设置该SearchView内默认显示的搜索文字
        searchView.setQueryHint("搜索");
        //为SearchView组件设置查询事件
        searchType = viewItem.findViewById(R.id.search_type);
        typeFood = viewItem.findViewById(R.id.type_food);
        foodFrame = viewItem.findViewById(R.id.food_frame);
        searchFoodRecyclerView = viewItem.findViewById(R.id.search_food_item);
        typePlace = viewItem.findViewById(R.id.type_place);
        placeFrame = viewItem.findViewById(R.id.place_frame);
        searchPlaceRecyclerView = viewItem.findViewById(R.id.search_place_item);
        typeAc = viewItem.findViewById(R.id.type_accommodation);
        acFrame = viewItem.findViewById(R.id.ac_frame);
        searchAcRecyclerView = viewItem.findViewById(R.id.search_ac_item);
        searchView.setOnQueryTextListener(new myOnQueryTextListener());
    }

    private void chooseCity() {
        //点击选择当前地区
        presentLocation = viewItem.findViewById(R.id.present_location);
        locationData = viewItem.getResources().getStringArray(R.array.location);
        locationAdapter = new ArrayAdapter<>(this.context, R.layout.location_item, locationData);
        presentLocation.setAdapter(locationAdapter);
        presentLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presentCity = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void tagClick() {
        indexAll = viewItem.findViewById(R.id.index_all);
        indexHotpot = viewItem.findViewById(R.id.index_hotpot);
        indexMeat = viewItem.findViewById(R.id.index_meat);
        indexForeign = viewItem.findViewById(R.id.index_foreign);
        indexRegion = viewItem.findViewById(R.id.index_region);
        indexOther = viewItem.findViewById(R.id.index_other);
        indexAll.setOnClickListener(new tagOnClickListener());
        indexHotpot.setOnClickListener(new tagOnClickListener());
        indexMeat.setOnClickListener(new tagOnClickListener());
        indexForeign.setOnClickListener(new tagOnClickListener());
        indexRegion.setOnClickListener(new tagOnClickListener());
        indexOther.setOnClickListener(new tagOnClickListener());
    }

    private void recommendClick() {
        //给推荐分类设置点击事件
        food = viewItem.findViewById(R.id.food);
        food.setOnClickListener(new myOnClickListener());
        place = viewItem.findViewById(R.id.place);
        place.setOnClickListener(new myOnClickListener());
        accommodation = viewItem.findViewById(R.id.accommodation);
        accommodation.setOnClickListener(new myOnClickListener());
    }

    private void initFilters() {
        //初始化过滤器数据
        BmobQuery<Filter> filterBmobQuery = new BmobQuery<Filter>();
        filterBmobQuery.findObjects(new FindListener<Filter>() {
            @Override
            public void done(List<Filter> flist, BmobException e) {
                if (e == null) {
//                    System.out.println("打印结果");
                    Log.d("filterList", "filter加载成功" + filterList.size());
                    for (Filter listItem : flist) {
                        filterList.add(listItem);
                    }
                    listAdapter = new FilterAdapter(RecommendationActivity.this.context, R.layout.fliter_list, filterList);
                    list.setAdapter(listAdapter);
                } else {
                    Log.d("filterList", "filter加载失败");
                }
            }
        });
    }


    private void initPlaces() {
        //初始化推荐景点数据
//        System.out.println("初始化推荐景点数据");
        BmobQuery<Place> placeBmobQuery = new BmobQuery<Place>();
        placeBmobQuery.addWhereEqualTo("city", presentCity);
        placeBmobQuery.findObjects(new FindListener<Place>() {

            @Override
            public void done(List<Place> list, BmobException e) {
                if (e == null) {
                    for (Place placeItem : list) {
                        placeList.add(placeItem);
//                        Log.d("placeList", "成功查询" + placeList.size());
//                        Log.d("placeImg", "placeImg: " + placeItem.getPlaceImg());
                    }
//                    System.out.println("placeList查询成功" + placeList.size());
                } else {
//                    System.out.println("placeList查询失败");
//                    Log.d("placeList", "place查询失败，placeList添加失败");
                }
            }
        });
    }

    private void initAccommodations() {
        //初始化住宿推荐数据
//        System.out.println("初始化住宿推荐数据");
        BmobQuery<Accommodation> acBmobQuery = new BmobQuery<Accommodation>();
        acBmobQuery.addWhereEqualTo("city", presentCity);
        acBmobQuery.findObjects(new FindListener<Accommodation>() {

            @Override
            public void done(List<Accommodation> list, BmobException e) {
                if (e == null) {
                    for (Accommodation acItem : list) {
                        acList.add(acItem);
//                        Log.d("acList", "成功查询" + acList.size());
                        //Log.d("acImg", "acImg: " + acItem.getAccommodationImg());
                    }
//                    System.out.println("acList查询成功" + acList.size());
                } else {
//                    Log.d("acList", "ac查询失败，acList添加失败");
//                    System.out.println("acList查询失败");

                }
            }
        });
    }

    class tagOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.index_all:
                    foodList.clear();
                    BmobQuery<Food> foodTypeBmobQuery = new BmobQuery<>();
                    foodTypeBmobQuery.addWhereEqualTo("city", presentCity);
                    foodTypeBmobQuery.findObjects(new FindListener<Food>() {
                        @Override
                        public void done(List<Food> list, BmobException e) {
                            if (e == null) {
                                for (Food foodItem : list) {
                                    foodList.add(foodItem);
                                    Log.d("allList", "成功查询" + foodList.size());
                                }
                            } else {
                                Log.d("allList", "筛选全部失败");
                            }
                            indexAdapter.notifyDataSetChanged();
                        }
                    });
                    break;
                case R.id.index_hotpot:
                    foodList.clear();
                    BmobQuery<Food> hotpotBmobQuery = new BmobQuery<>();
                    hotpotBmobQuery.addWhereEqualTo("city", presentCity);
                    hotpotBmobQuery.addWhereEqualTo("type", "火锅");
                    hotpotBmobQuery.findObjects(new FindListener<Food>() {
                        @Override
                        public void done(List<Food> list, BmobException e) {
                            if (e == null) {
                                for (Food foodItem : list) {
                                    foodList.add(foodItem);
                                    Log.d("hotpotList", "成功查询" + foodList.size());
                                }
                            } else {
                                Log.d("hotpotList", "筛选火锅类失败");
                            }
                            indexAdapter.notifyDataSetChanged();
                        }
                    });
                    break;
                case R.id.index_meat:
                    foodList.clear();
                    BmobQuery<Food> meatBmobQuery = new BmobQuery<>();
                    meatBmobQuery.addWhereEqualTo("city", presentCity);
                    meatBmobQuery.addWhereEqualTo("type", "烧烤");
                    meatBmobQuery.findObjects(new FindListener<Food>() {
                        @Override
                        public void done(List<Food> list, BmobException e) {
                            if (e == null) {
                                for (Food foodItem : list) {
                                    foodList.add(foodItem);
                                    Log.d("meatList", "成功查询" + foodList.size());
                                }
                            } else {
                                Log.d("meatList", "筛选烤肉类失败");
                            }
                            indexAdapter.notifyDataSetChanged();
                        }
                    });
                    break;
                case R.id.index_foreign:
                    foodList.clear();
                    BmobQuery<Food> foreignBmobQuery = new BmobQuery<>();
                    foreignBmobQuery.addWhereEqualTo("city", presentCity);
                    foreignBmobQuery.addWhereEqualTo("type", "异域料理");
                    foreignBmobQuery.findObjects(new FindListener<Food>() {
                        @Override
                        public void done(List<Food> list, BmobException e) {
                            if (e == null) {
                                for (Food foodItem : list) {
                                    foodList.add(foodItem);
                                    Log.d("foreignList", "成功查询" + foodList.size());
                                }
                            } else {
                                Log.d("foreignList", "筛选异域料理类失败");
                            }
                            indexAdapter.notifyDataSetChanged();
                        }
                    });
                    break;
                case R.id.index_region:
                    foodList.clear();
                    BmobQuery<Food> regionBmobQuery = new BmobQuery<>();
                    regionBmobQuery.addWhereEqualTo("city", presentCity);
                    regionBmobQuery.addWhereEqualTo("type", "地方特色");
                    regionBmobQuery.findObjects(new FindListener<Food>() {
                        @Override
                        public void done(List<Food> list, BmobException e) {
                            if (e == null) {
                                for (Food foodItem : list) {
                                    foodList.add(foodItem);
                                    Log.d("regionList", "成功查询" + foodList.size());
                                }
                            } else {
                                Log.d("regionList", "筛选地方特色类失败");
                            }
                            indexAdapter.notifyDataSetChanged();
                        }
                    });
                    break;
                case R.id.index_other:
                    foodList.clear();
                    BmobQuery<Food> otherBmobQuery = new BmobQuery<>();
                    otherBmobQuery.addWhereEqualTo("city", presentCity);
                    otherBmobQuery.addWhereEqualTo("type", "其他");
                    otherBmobQuery.findObjects(new FindListener<Food>() {
                        @Override
                        public void done(List<Food> list, BmobException e) {
                            if (e == null) {
                                for (Food foodItem : list) {
                                    foodList.add(foodItem);
                                    Log.d("otherList", "成功查询" + foodList.size());
                                }
                            } else {
                                Log.d("otherList", "筛选其他类失败");
                            }
                            indexAdapter.notifyDataSetChanged();
                        }
                    });
                    break;

            }
        }
    }

    class myOnQueryTextListener implements SearchView.OnQueryTextListener {
        //搜索栏查询监听器实现
        FoodAdapter foodAdapter;
        PlaceAdapter placeAdapter = new PlaceAdapter(placeList, RecommendationActivity.this.context);
        AccommodationAdapter acAdapter = new AccommodationAdapter(acList, RecommendationActivity.this.context);

        //单击搜索按钮时激发该方法
        @Override
        public boolean onQueryTextSubmit(String query) {
            if (query != null) {
                BmobQuery<Food> searchFoodBmobQuery = new BmobQuery<Food>();
                searchFoodBmobQuery.addWhereEqualTo("city", presentCity);
                //searchBmobQuery.addWhereContains("name", query);
                searchFoodBmobQuery.addWhereEqualTo("name", query);
                searchFoodBmobQuery.findObjects(new FindListener<Food>() {

                    @Override
                    public void done(List<Food> list, BmobException e) {
                        if (e == null) {
                            foodList.clear();
                            for (Food foodItem : list) {
                                foodList.add(foodItem);
                                Log.d("foodList", "成功添加" + foodList.size());
                                //Log.d("foodImg", "foodImg: " + foodItem.getFoodImg());
                            }
                            foodAdapter = new FoodAdapter(foodList, RecommendationActivity.this.context);
                            StaggeredGridLayoutManager searchLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                            searchFoodRecyclerView.setLayoutManager(searchLayoutManager);
                            searchFoodRecyclerView.setAdapter(foodAdapter);
//                            System.out.println("筛选结束");
                        } else {
                            Log.d("foodList", "food查询失败，foodList添加失败");
                        }
                    }
                });

                searchType.setVisibility(View.VISIBLE);
                foodFrame.setVisibility(View.VISIBLE);
                searchFoodRecyclerView.setVisibility(View.VISIBLE);
//                System.out.println("设置可见");

                typeFood.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acFrame.setVisibility(View.INVISIBLE);
                        searchAcRecyclerView.setVisibility(View.INVISIBLE);
                        placeFrame.setVisibility(View.INVISIBLE);
                        searchPlaceRecyclerView.setVisibility(View.INVISIBLE);
                        //查询美食部分
                        BmobQuery<Food> searchFoodBmobQuery = new BmobQuery<Food>();
                        searchFoodBmobQuery.addWhereEqualTo("city", presentCity);
                        //searchBmobQuery.addWhereContains("name", query);
                        searchFoodBmobQuery.addWhereEqualTo("name", query);
                        searchFoodBmobQuery.findObjects(new FindListener<Food>() {
                            @Override
                            public void done(List<Food> list, BmobException e) {
                                if (e == null) {
                                    foodList.clear();
                                    for (Food foodItem : list) {
                                        foodList.add(foodItem);
                                        Log.d("foodList", "成功添加" + foodList.size());
                                        //Log.d("foodImg", "foodImg: " + foodItem.getFoodImg());
                                    }
                                } else {
                                    Log.d("foodList", "food查询失败，foodList添加失败");
                                }
                            }
                        });
                        foodAdapter.notifyDataSetChanged();
                        StaggeredGridLayoutManager searchLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        searchFoodRecyclerView.setLayoutManager(searchLayoutManager);
                        searchFoodRecyclerView.setAdapter(foodAdapter);

                        foodFrame.setVisibility(View.VISIBLE);
                        searchFoodRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
                typePlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acFrame.setVisibility(View.INVISIBLE);
                        searchAcRecyclerView.setVisibility(View.INVISIBLE);
                        foodFrame.setVisibility(View.INVISIBLE);
                        searchFoodRecyclerView.setVisibility(View.INVISIBLE);
                        //查询景点部分
                        BmobQuery<Place> searchPlaceBmobQuery = new BmobQuery<Place>();
                        //searchBmobQuery.addWhereContains("name", query);
                        searchPlaceBmobQuery.addWhereEqualTo("name", query);
                        searchPlaceBmobQuery.addWhereEqualTo("city", presentCity);
                        searchPlaceBmobQuery.findObjects(new FindListener<Place>() {

                            @Override
                            public void done(List<Place> list, BmobException e) {
                                if (e == null) {
                                    placeList.clear();
                                    for (Place placeItem : list) {
                                        placeList.add(placeItem);
                                        Log.d("placeList", "成功查询" + placeList.size());
                                        //Log.d("placeImg", "placeImg: " + placeItem.getPlaceImg());
                                    }
                                } else {
                                    Log.d("placeList", "place查询失败，placeList添加失败");
                                }
                            }
                        });
                        placeAdapter.notifyDataSetChanged();
                        StaggeredGridLayoutManager placeLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        searchPlaceRecyclerView.setLayoutManager(placeLayoutManager);
                        searchPlaceRecyclerView.setAdapter(placeAdapter);

                        placeFrame.setVisibility(View.VISIBLE);
                        searchPlaceRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
                typeAc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acFrame.setVisibility(View.INVISIBLE);
                        searchAcRecyclerView.setVisibility(View.INVISIBLE);
                        foodFrame.setVisibility(View.INVISIBLE);
                        searchFoodRecyclerView.setVisibility(View.INVISIBLE);
                        //查询住宿部分
                        BmobQuery<Accommodation> searchAcBmobQuery = new BmobQuery<Accommodation>();
                        //searchBmobQuery.addWhereContains("name", query);
                        searchAcBmobQuery.addWhereEqualTo("name", query);
                        searchAcBmobQuery.addWhereEqualTo("city", presentCity);
                        searchAcBmobQuery.findObjects(new FindListener<Accommodation>() {

                            @Override
                            public void done(List<Accommodation> list, BmobException e) {
                                if (e == null) {
                                    acList.clear();
                                    for (Accommodation acItem : list) {
                                        acList.add(acItem);
                                        Log.d("acList", "成功查询" + acList.size());
                                        //Log.d("acImg", "acImg: " + acItem.getAccommodationImg());
                                    }
                                } else {
                                    Log.d("acList", "ac查询失败，acList添加失败");
                                }
                            }
                        });
                        acAdapter.notifyDataSetChanged();

                        StaggeredGridLayoutManager acLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        searchAcRecyclerView.setLayoutManager(acLayoutManager);
                        searchAcRecyclerView.setAdapter(acAdapter);

                        acFrame.setVisibility(View.VISIBLE);
                        searchAcRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }

            return false;
        }

        //用户输入时激发该方法
        @Override
        public boolean onQueryTextChange(String newText) {

            if (TextUtils.isEmpty(newText)) {//如果newText是长度为0的字符串
                //清除ListView的过滤
                list.clearTextFilter();
                list.setVisibility(View.INVISIBLE);

                //恢复美食初始数据
                BmobQuery<Food> searchBmobQuery = new BmobQuery<Food>();
                searchBmobQuery.addWhereEqualTo("city", presentCity);
                searchBmobQuery.findObjects(new FindListener<Food>() {

                    @Override
                    public void done(List<Food> list, BmobException e) {
                        if (e == null) {
                            foodList.clear();
                            for (Food foodItem : list) {
                                foodList.add(foodItem);
                                Log.d("foodList", "成功添加" + foodList.size());
                                //Log.d("foodImg", "foodImg: " + foodItem.getFoodImg());
                            }
                        } else {
                            Log.d("foodList", "food查询失败，foodList添加失败");
                        }
                    }
                });
                //恢复景点初始数据
                BmobQuery<Place> searchPlaceBmobQuery = new BmobQuery<Place>();
                searchPlaceBmobQuery.addWhereEqualTo("city", presentCity);
                searchPlaceBmobQuery.findObjects(new FindListener<Place>() {

                    @Override
                    public void done(List<Place> list, BmobException e) {
                        if (e == null) {
                            foodList.clear();
                            for (Place placeItem : list) {
                                placeList.add(placeItem);
                                Log.d("placeList", "成功查询" + placeList.size());
                                //Log.d("placeImg", "placeImg: " + placeItem.getPlaceImg());
                            }
                        } else {
                            Log.d("placeList", "place查询失败，placeList添加失败");
                        }
                    }
                });
                //恢复住宿初始数据
                BmobQuery<Accommodation> searchAcBmobQuery = new BmobQuery<Accommodation>();
                searchAcBmobQuery.addWhereEqualTo("city", presentCity);
                searchAcBmobQuery.findObjects(new FindListener<Accommodation>() {

                    @Override
                    public void done(List<Accommodation> list, BmobException e) {
                        if (e == null) {
                            acList.clear();
                            for (Accommodation acItem : list) {
                                acList.add(acItem);
                                Log.d("acList", "成功查询" + acList.size());
                                //Log.d("acImg", "acImg: " + acItem.getAccommodationImg());
                            }
                        } else {
                            Log.d("acList", "ac查询失败，acList添加失败");
                        }
                    }
                });
                foodAdapter = new FoodAdapter(foodList, RecommendationActivity.this.context);
                foodAdapter.notifyDataSetChanged();
                placeAdapter.notifyDataSetChanged();
                acAdapter.notifyDataSetChanged();
                //查询完毕返回推荐首页
                //searchFrame.setVisibility(View.INVISIBLE);
                searchType.setVisibility(View.INVISIBLE);
                foodFrame.setVisibility(View.INVISIBLE);
                placeFrame.setVisibility(View.INVISIBLE);
                acFrame.setVisibility(View.INVISIBLE);
            } else {
                //使用用户输入的内容对ListView的列表项进行过滤
                //list.setFilterText(newText);

                listAdapter.getFilter().filter(newText);
                listAdapter.notifyDataSetChanged();
                //listAdapter.getFilter().filter(newText);
                searchType.setVisibility(View.VISIBLE);
                foodFrame.setVisibility(View.VISIBLE);
                placeFrame.setVisibility(View.VISIBLE);
                acFrame.setVisibility(View.VISIBLE);
                list.setVisibility(View.VISIBLE);
            }
            return true;
        }
    }

    class myOnClickListener implements View.OnClickListener {

        //推荐类别的点击事件实现
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.food:
                    //初始化数据
                    BmobQuery<Food> searchBmobQuery = new BmobQuery<Food>();
                    searchBmobQuery.addWhereEqualTo("city", presentCity);
                    //searchBmobQuery.addWhereContains("name", query);
                    searchBmobQuery.findObjects(new FindListener<Food>() {

                        @Override
                        public void done(List<Food> list, BmobException e) {
                            if (e == null) {
                                foodList.clear();
                                for (Food foodItem : list) {
                                    foodList.add(foodItem);
                                    Log.d("foodList", "成功添加" + foodList.size());
                                    //Log.d("foodImg", "foodImg: " + foodItem.getFoodImg());
                                }
                            } else {
                                Log.d("foodList", "food查询失败，foodList添加失败");
                            }
                        }
                    });

                    FoodAdapter foodAdapter = new FoodAdapter(foodList, RecommendationActivity.this.context);
                    foodAdapter.notifyDataSetChanged();
                    condition.removeAllViewsInLayout();

                    //添加标签栏
                    LinearLayout foodTag = new LinearLayout(getApplicationContext());
                    foodTag.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams foodTagParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80);
                    foodTagParams.setMargins(0, 0, 0, 0);
                    foodTag.setGravity(Gravity.CENTER);
                    //foodTagParams.weight = 1;
                    foodTag.setLayoutParams(foodTagParams);
                    TextView foodAll = new TextView(getApplicationContext());
                    TextView hotpot = new TextView(getApplicationContext());
                    TextView meat = new TextView(getApplicationContext());
                    TextView foreign = new TextView(getApplicationContext());
                    TextView region = new TextView(getApplicationContext());
                    TextView other = new TextView(getApplicationContext());
                    LinearLayout.LayoutParams foodTextParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                    foodTextParams.weight = 1;

                    //foodAll.setId(getResources().getIdentifier("foodAll", "id", getPackageName()));
                    foodAll.setText("所有");
                    foodAll.setTextColor(Color.parseColor("#919191"));
                    foodAll.setLayoutParams(foodTextParams);
                    //hotpot.setId(getResources().getIdentifier("hotpot", "id", getPackageName()));
                    hotpot.setText("火锅");
                    hotpot.setTextColor(Color.parseColor("#919191"));
                    hotpot.setLayoutParams(foodTextParams);
                    //meat.setId(getResources().getIdentifier("meat", "id", getPackageName()));
                    meat.setText("烧烤");
                    meat.setTextColor(Color.parseColor("#919191"));
                    meat.setLayoutParams(foodTextParams);
                    //foreign.setId(getResources().getIdentifier("foodAll", "id", getPackageName()));
                    foreign.setText("异域料理");
                    foreign.setTextColor(Color.parseColor("#919191"));
                    foreign.setLayoutParams(foodTextParams);
                    //region.setId(getResources().getIdentifier("foodAll", "id", getPackageName()));
                    region.setText("地方特色");
                    region.setTextColor(Color.parseColor("#919191"));
                    region.setLayoutParams(foodTextParams);
                    //other.setId(getResources().getIdentifier("foodAll", "id", getPackageName()));
                    other.setText("其他");
                    other.setTextColor(Color.parseColor("#919191"));
                    other.setLayoutParams(foodTextParams);
                    foodTag.addView(foodAll, 0);
                    foodTag.addView(hotpot, 1);
                    foodTag.addView(meat, 2);
                    foodTag.addView(foreign, 3);
                    foodTag.addView(region, 4);
                    foodTag.addView(other, 5);
                    condition.addView(foodTag, 0);

                    //加载美食推荐数据
                    RecyclerView foodRecyclerView = new RecyclerView(getApplicationContext());
                    foodRecyclerView.setAdapter(foodAdapter);
                    StaggeredGridLayoutManager foodLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    foodRecyclerView.setLayoutManager(foodLayoutManager);
                    condition.addView(foodRecyclerView, 1);
                    LinearLayout.LayoutParams foodParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                    foodParams.weight = 6;
                    foodRecyclerView.setLayoutParams(foodParams);

                    //点击标签筛选
                    foodAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            foodList.clear();
                            BmobQuery<Food> foodTypeBmobQuery = new BmobQuery<>();
                            foodTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            foodTypeBmobQuery.findObjects(new FindListener<Food>() {
                                @Override
                                public void done(List<Food> list, BmobException e) {
                                    if (e == null) {
                                        for (Food foodItem : list) {
                                            foodList.add(foodItem);
                                            Log.d("allList", "成功查询" + foodList.size());
                                        }
                                    } else {
                                        Log.d("allList", "筛选全部失败");
                                    }
                                    foodAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    hotpot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            foodList.clear();
                            BmobQuery<Food> foodTypeBmobQuery = new BmobQuery<>();
                            foodTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            foodTypeBmobQuery.addWhereEqualTo("type", "火锅");
                            foodTypeBmobQuery.findObjects(new FindListener<Food>() {
                                @Override
                                public void done(List<Food> list, BmobException e) {
                                    if (e == null) {
                                        for (Food foodItem : list) {
                                            foodList.add(foodItem);
                                            Log.d("hotpotList", "成功查询" + foodList.size());
                                        }
                                    } else {
                                        Log.d("hotpotList", "筛选火锅类失败");
                                    }
                                    foodAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    meat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            foodList.clear();
                            BmobQuery<Food> foodTypeBmobQuery = new BmobQuery<>();
                            foodTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            foodTypeBmobQuery.addWhereEqualTo("type", "烧烤");
                            foodTypeBmobQuery.findObjects(new FindListener<Food>() {
                                @Override
                                public void done(List<Food> list, BmobException e) {
                                    if (e == null) {
                                        for (Food foodItem : list) {
                                            foodList.add(foodItem);
                                            Log.d("meatList", "成功查询" + foodList.size());
                                        }
                                    } else {
                                        Log.d("meatList", "筛选烤肉类失败");
                                    }
                                    foodAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    foreign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            foodList.clear();
                            BmobQuery<Food> foodTypeBmobQuery = new BmobQuery<>();
                            foodTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            foodTypeBmobQuery.addWhereEqualTo("type", "异域料理");
                            foodTypeBmobQuery.findObjects(new FindListener<Food>() {
                                @Override
                                public void done(List<Food> list, BmobException e) {
                                    if (e == null) {
                                        for (Food foodItem : list) {
                                            foodList.add(foodItem);
                                            Log.d("foreignList", "成功查询" + foodList.size());
                                        }
                                    } else {
                                        Log.d("foreignList", "筛选异域料理类失败");
                                    }
                                    foodAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    region.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            foodList.clear();
                            BmobQuery<Food> foodTypeBmobQuery = new BmobQuery<>();
                            foodTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            foodTypeBmobQuery.addWhereEqualTo("type", "地方特色");
                            foodTypeBmobQuery.findObjects(new FindListener<Food>() {
                                @Override
                                public void done(List<Food> list, BmobException e) {
                                    if (e == null) {
                                        for (Food foodItem : list) {
                                            foodList.add(foodItem);
                                            Log.d("regionList", "成功查询" + foodList.size());
                                        }
                                    } else {
                                        Log.d("regionList", "筛选地方特色类失败");
                                    }
                                    foodAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    other.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            foodList.clear();
                            BmobQuery<Food> foodTypeBmobQuery = new BmobQuery<>();
                            foodTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            foodTypeBmobQuery.addWhereEqualTo("type", "其他");
                            foodTypeBmobQuery.findObjects(new FindListener<Food>() {
                                @Override
                                public void done(List<Food> list, BmobException e) {
                                    if (e == null) {
                                        for (Food foodItem : list) {
                                            foodList.add(foodItem);
                                            Log.d("otherList", "成功查询" + foodList.size());
                                        }
                                    } else {
                                        Log.d("otherList", "筛选其他类失败");
                                    }
                                    foodAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    break;

                case R.id.place:
                    //初始化景点数据
                    BmobQuery<Place> placeTypeBmobQuery = new BmobQuery<>();
                    placeTypeBmobQuery.addWhereEqualTo("city", presentCity);
                    placeTypeBmobQuery.findObjects(new FindListener<Place>() {
                        @Override
                        public void done(List<Place> list, BmobException e) {
                            if (e == null) {
                                placeList.clear();
                                for (Place placeItem : list) {
                                    placeList.add(placeItem);
                                    Log.d("allPlaceList", "成功查询" + placeList.size());
                                }
                            } else {
                                Log.d("allPlaceList", "筛选所有景点类失败");
                            }

                        }
                    });
                    PlaceAdapter placeAdapter = new PlaceAdapter(placeList, RecommendationActivity.this.context);
                    placeAdapter.notifyDataSetChanged();

                    //设置标签栏
                    condition.removeAllViewsInLayout();
                    LinearLayout placeTag = new LinearLayout(getApplicationContext());
                    placeTag.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams placeTagParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80);
                    placeTagParams.setMargins(0, 0, 0, 0);
                    placeTag.setGravity(Gravity.CENTER);
                    //placeTagParams.weight = 1;
                    placeTag.setLayoutParams(placeTagParams);
                    TextView placeAll = new TextView(getApplicationContext());
                    TextView nature = new TextView(getApplicationContext());
                    TextView show = new TextView(getApplicationContext());
                    TextView park = new TextView(getApplicationContext());
                    TextView famous = new TextView(getApplicationContext());
                    LinearLayout.LayoutParams placeTextParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                    placeTextParams.weight = 1;

                    placeAll.setText("所有");
                    placeAll.setTextColor(Color.parseColor("#919191"));
                    placeAll.setLayoutParams(placeTextParams);
                    nature.setText("自然风光");
                    nature.setTextColor(Color.parseColor("#919191"));
                    nature.setLayoutParams(placeTextParams);
                    show.setText("展馆展览");
                    show.setTextColor(Color.parseColor("#919191"));
                    show.setLayoutParams(placeTextParams);
                    park.setText("主题乐园");
                    park.setTextColor(Color.parseColor("#919191"));
                    park.setLayoutParams(placeTextParams);
                    famous.setText("名胜古迹");
                    famous.setTextColor(Color.parseColor("#919191"));
                    famous.setLayoutParams(placeTextParams);

                    placeTag.addView(placeAll, 0);
                    placeTag.addView(nature, 1);
                    placeTag.addView(show, 2);
                    placeTag.addView(park, 3);
                    placeTag.addView(famous, 4);
                    condition.addView(placeTag, 0);

                    //加载景点推荐数据
                    RecyclerView placeRecyclerView = new RecyclerView(getApplicationContext());
                    StaggeredGridLayoutManager placeLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    placeRecyclerView.setLayoutManager(placeLayoutManager);
                    //PlaceAdapter placeAdapter = new PlaceAdapter(placeList, RecommendationActivity.this);
                    placeRecyclerView.setAdapter(placeAdapter);
                    condition.addView(placeRecyclerView, 1);
                    LinearLayout.LayoutParams placeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                    placeParams.weight = 6;
                    placeRecyclerView.setLayoutParams(placeParams);

                    //点击标签筛选
                    placeAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            placeList.clear();
                            BmobQuery<Place> placeTypeBmobQuery = new BmobQuery<>();
                            placeTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            placeTypeBmobQuery.findObjects(new FindListener<Place>() {
                                @Override
                                public void done(List<Place> list, BmobException e) {
                                    if (e == null) {
                                        for (Place placeItem : list) {
                                            placeList.add(placeItem);
                                            Log.d("allPlaceList", "成功查询" + placeList.size());
                                        }
                                    } else {
                                        Log.d("allPlaceList", "筛选所有景点类失败");
                                    }
                                    placeAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    nature.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            placeList.clear();
                            BmobQuery<Place> placeTypeBmobQuery = new BmobQuery<>();
                            placeTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            placeTypeBmobQuery.addWhereEqualTo("type", "自然风光");
                            placeTypeBmobQuery.findObjects(new FindListener<Place>() {
                                @Override
                                public void done(List<Place> list, BmobException e) {
                                    if (e == null) {
                                        for (Place placeItem : list) {
                                            placeList.add(placeItem);
                                            Log.d("natureList", "成功查询" + placeList.size());
                                        }
                                    } else {
                                        Log.d("natureList", "筛选自然风光类失败");
                                    }
                                    placeAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    show.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            placeList.clear();
                            BmobQuery<Place> placeTypeBmobQuery = new BmobQuery<>();
                            placeTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            placeTypeBmobQuery.addWhereEqualTo("type", "展馆展览");
                            placeTypeBmobQuery.findObjects(new FindListener<Place>() {
                                @Override
                                public void done(List<Place> list, BmobException e) {
                                    if (e == null) {
                                        for (Place placeItem : list) {
                                            placeList.add(placeItem);
                                            Log.d("showList", "成功查询" + placeList.size());
                                        }
                                    } else {
                                        Log.d("showList", "筛选展馆展览类失败");
                                    }
                                    placeAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    park.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            placeList.clear();
                            BmobQuery<Place> placeTypeBmobQuery = new BmobQuery<>();
                            placeTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            placeTypeBmobQuery.addWhereEqualTo("type", "主题乐园");
                            placeTypeBmobQuery.findObjects(new FindListener<Place>() {
                                @Override
                                public void done(List<Place> list, BmobException e) {
                                    if (e == null) {
                                        for (Place placeItem : list) {
                                            placeList.add(placeItem);
                                            Log.d("parkList", "成功查询" + placeList.size());
                                        }
                                    } else {
                                        Log.d("parkList", "筛选主题乐园类失败");
                                    }
                                    placeAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    famous.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            placeList.clear();
                            BmobQuery<Place> placeTypeBmobQuery = new BmobQuery<>();
                            placeTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            placeTypeBmobQuery.addWhereEqualTo("type", "名胜古迹");
                            placeTypeBmobQuery.findObjects(new FindListener<Place>() {
                                @Override
                                public void done(List<Place> list, BmobException e) {
                                    if (e == null) {
                                        for (Place placeItem : list) {
                                            placeList.add(placeItem);
                                            Log.d("famousList", "成功查询" + placeList.size());
                                        }
                                    } else {
                                        Log.d("famousList", "筛选名胜古迹类失败");
                                    }
                                    placeAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    break;

                case R.id.accommodation:
                    //初始化住宿推荐数据
                    BmobQuery<Accommodation> acTypeBmobQuery = new BmobQuery<>();
                    acTypeBmobQuery.addWhereEqualTo("city", presentCity);
                    acTypeBmobQuery.findObjects(new FindListener<Accommodation>() {
                        @Override
                        public void done(List<Accommodation> list, BmobException e) {
                            if (e == null) {
                                acList.clear();
                                for (Accommodation acItem : list) {
                                    acList.add(acItem);
                                    Log.d("allAcList", "成功查询" + acList.size());
                                }
                            } else {
                                Log.d("allAcList", "筛选所有住宿类失败");
                            }

                        }
                    });
                    AccommodationAdapter acAdapter = new AccommodationAdapter(acList, RecommendationActivity.this.context);
                    acAdapter.notifyDataSetChanged();

                    //设置标签栏
                    condition.removeAllViewsInLayout();
                    LinearLayout accommodationTag = new LinearLayout(getApplicationContext());
                    accommodationTag.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams accommodationTagParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80);
                    accommodationTagParams.setMargins(0, 0, 0, 0);
                    accommodationTag.setGravity(Gravity.CENTER);
                    accommodationTag.setLayoutParams(accommodationTagParams);
                    TextView accommodationAll = new TextView(getApplicationContext());
                    TextView ms = new TextView(getApplicationContext());
                    TextView ql = new TextView(getApplicationContext());
                    TextView jd = new TextView(getApplicationContext());
                    LinearLayout.LayoutParams acTextParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                    acTextParams.weight = 1;
//                    accommodationAll.setTextSize(20);
//                    ms.setTextSize(20);
//                    ql.setTextSize(20);
//                    jd.setTextSize(20);
                    accommodationAll.setText("所有");
                    accommodationAll.setTextColor(Color.parseColor("#919191"));
                    accommodationAll.setLayoutParams(acTextParams);
                    ms.setText("民宿");
                    ms.setTextColor(Color.parseColor("#919191"));
                    ms.setLayoutParams(acTextParams);
                    ql.setText("青年旅舍");
                    ql.setTextColor(Color.parseColor("#919191"));
                    ql.setLayoutParams(acTextParams);
                    jd.setText("酒店");
                    jd.setTextColor(Color.parseColor("#919191"));
                    jd.setLayoutParams(acTextParams);

                    accommodationTag.addView(accommodationAll, 0);
                    accommodationTag.addView(ms, 1);
                    accommodationTag.addView(ql, 2);
                    accommodationTag.addView(jd, 3);
                    condition.addView(accommodationTag, 0);

                    //加载住宿推荐数据
                    RecyclerView acRecyclerView = new RecyclerView(getApplicationContext());
                    StaggeredGridLayoutManager acLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    acRecyclerView.setLayoutManager(acLayoutManager);
                    //AccommodationAdapter acAdapter = new AccommodationAdapter(acList, RecommendationActivity.this);
                    acRecyclerView.setAdapter(acAdapter);
                    condition.addView(acRecyclerView, 1);
                    LinearLayout.LayoutParams acParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                    acParams.weight = 6;
                    acRecyclerView.setLayoutParams(acParams);

                    //点击标签筛选
                    accommodationAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acList.clear();
                            BmobQuery<Accommodation> acTypeBmobQuery = new BmobQuery<>();
                            acTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            acTypeBmobQuery.findObjects(new FindListener<Accommodation>() {
                                @Override
                                public void done(List<Accommodation> list, BmobException e) {
                                    if (e == null) {
                                        for (Accommodation acItem : list) {
                                            acList.add(acItem);
                                            Log.d("allAcList", "成功查询" + acList.size());
                                        }
                                    } else {
                                        Log.d("allAcList", "筛选所有住宿类失败");
                                    }
                                    acAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    ms.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acList.clear();
                            BmobQuery<Accommodation> acTypeBmobQuery = new BmobQuery<>();
                            acTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            acTypeBmobQuery.addWhereEqualTo("type", "民宿");
                            acTypeBmobQuery.findObjects(new FindListener<Accommodation>() {
                                @Override
                                public void done(List<Accommodation> list, BmobException e) {
                                    if (e == null) {
                                        for (Accommodation acItem : list) {
                                            acList.add(acItem);
                                            Log.d("msList", "成功查询" + acList.size());
                                        }
                                    } else {
                                        Log.d("msList", "筛选民宿类失败");
                                    }
                                    acAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    ql.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acList.clear();
                            BmobQuery<Accommodation> acTypeBmobQuery = new BmobQuery<>();
                            acTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            acTypeBmobQuery.addWhereEqualTo("type", "青年旅舍");
                            acTypeBmobQuery.findObjects(new FindListener<Accommodation>() {
                                @Override
                                public void done(List<Accommodation> list, BmobException e) {
                                    if (e == null) {
                                        for (Accommodation acItem : list) {
                                            acList.add(acItem);
                                            Log.d("qlList", "成功查询" + acList.size());
                                        }
                                    } else {
                                        Log.d("qlList", "筛选青年旅舍类失败");
                                    }
                                    acAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    jd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acList.clear();
                            BmobQuery<Accommodation> acTypeBmobQuery = new BmobQuery<>();
                            acTypeBmobQuery.addWhereEqualTo("city", presentCity);
                            acTypeBmobQuery.addWhereEqualTo("type", "酒店");
                            acTypeBmobQuery.findObjects(new FindListener<Accommodation>() {
                                @Override
                                public void done(List<Accommodation> list, BmobException e) {
                                    if (e == null) {
                                        for (Accommodation acItem : list) {
                                            acList.add(acItem);
                                            Log.d("jdList", "成功查询" + acList.size());
                                        }
                                    } else {
                                        Log.d("jdList", "筛选酒店类失败");
                                    }
                                    acAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    break;
            }
        }
    }

}
