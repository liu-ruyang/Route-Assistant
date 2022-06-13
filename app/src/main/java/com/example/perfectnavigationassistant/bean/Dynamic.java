package com.example.perfectnavigationassistant.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class Dynamic extends BmobObject {

    private User userId;
    private BmobDate publishTime;
    private String dynamicContent;
    private Integer love;

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public BmobDate getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(BmobDate publish_time) {
        this.publishTime = publish_time;
    }

    public String getDynamicContent() {
        return dynamicContent;
    }

    public void setDynamicContent(String dynamicContent) {
        this.dynamicContent = dynamicContent;
    }

    public Integer getLove() {
        return love;
    }

    public void setLove(Integer love) {
        this.love = love;
    }

    @Override
    public String toString() {
        return "Dynamic{" +
                "userId=" + userId +
                ", publishTime=" + publishTime +
                ", dynamicContent='" + dynamicContent + '\'' +
                ", love=" + love +
                '}';
    }
}
