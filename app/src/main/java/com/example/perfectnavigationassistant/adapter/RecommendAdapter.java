package com.example.perfectnavigationassistant.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class RecommendAdapter extends FragmentPagerAdapter {

    private ArrayList<String> title_list;//放置TabLayout的标题
    private ArrayList<Fragment> fragment_list;//放置ViewPager容器下的Fragment

    // 构造方法传参：fragment管理器、标题列表、fragment控件列表
    public RecommendAdapter(FragmentManager fm, ArrayList<String> title_list, ArrayList<Fragment> fragment_list) {
        super(fm);
        this.title_list = title_list;
        this.fragment_list = fragment_list;
    }

    //根据参数，获取一个fragment
    @NonNull
    public Fragment getItem(int a) {
        return fragment_list.get(a);
    }

    //获取fragment控件列表的大小（有几个fragment“碎片”）
    @Override
    public int getCount() {
        return fragment_list.size();
    }

    //获取标题位置
    @Override
    public CharSequence getPageTitle(int a) {
        return title_list.get(a);
    }
}
