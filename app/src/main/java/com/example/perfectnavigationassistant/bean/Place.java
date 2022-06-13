package com.example.perfectnavigationassistant.bean;

import cn.bmob.v3.BmobObject;

public class Place extends BmobObject {

    private String city;
    private String type;
    private String name;
    private String reason;
    private String location;
    //private BmobFile placeImg;
    private String placeImg;

    public Place(String city, String type, String name, String reason, String location, String placeImg) {
        this.city = city;
        this.type = type;
        this.name = name;
        this.reason = reason;
        this.location = location;
        this.placeImg = placeImg;
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

//    public BmobFile getPlaceImg() {
//        return placeImg;
//    }
//
//    public void setPlaceImg(BmobFile placeImg) {
//        this.placeImg = placeImg;
//    }

    public String getPlaceImg() {
        return placeImg;
    }

    public void setPlaceImg(String placeImg) {
        this.placeImg = placeImg;
    }

    @Override
    public String toString() {
        return "Place{" +
                "city='" + city + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", reason='" + reason + '\'' +
                ", location='" + location + '\'' +
                ", placeImg='" + placeImg + '\'' +
                '}';
    }
}
