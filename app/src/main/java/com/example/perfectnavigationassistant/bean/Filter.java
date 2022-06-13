package com.example.perfectnavigationassistant.bean;

import cn.bmob.v3.BmobObject;

public class Filter extends BmobObject {
    private String recommendContext;

    public String getRecommendContext() {
        return recommendContext;
    }

    public void setRecommendContext(String recommendContext) {
        this.recommendContext = recommendContext;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "recommendContext='" + recommendContext + '\'' +
                '}';
    }
}
