package com.example.perfectnavigationassistant.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class Search extends BmobObject {

    private List<String> searchContent;
    private Integer userId;

    public List<String> getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(List<String> searchContent) {
        this.searchContent = searchContent;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Search{" +
                "searchContent=" + searchContent +
                ", userId=" + userId +
                '}';
    }
}
