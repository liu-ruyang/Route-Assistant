package com.example.perfectnavigationassistant.bean;

import cn.bmob.v3.BmobObject;

public class Food extends BmobObject {

    private String city;
    private String type;
    private String name;
    private String reason;
    private String location;
    private String foodImg;


    public Food(String city, String type, String name, String reason, String location, String foodImg) {
        this.city = city;
        this.type = type;
        this.name = name;
        this.reason = reason;
        this.location = location;
        this.foodImg = foodImg;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFoodImg() {
        return foodImg;
    }

    public void setFoodImg(String foodImg) {
        this.foodImg = foodImg;
    }

    @Override
    public String toString() {
        return "Food{" +
                "city='" + city + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", reason='" + reason + '\'' +
                ", location='" + location + '\'' +
                ", foodImg='" + foodImg + '\'' +
                '}';
    }
}
