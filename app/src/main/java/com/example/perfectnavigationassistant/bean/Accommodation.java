package com.example.perfectnavigationassistant.bean;

import cn.bmob.v3.BmobObject;

public class Accommodation extends BmobObject {

    private String city;
    private String type;
    private String name;
    private String reason;
    private String location;
    //private BmobFile accommodationImg;
    private String accommodationImg;

    public Accommodation(String city, String type, String name, String reason, String location, String accommodationImg) {
        this.city = city;
        this.type = type;
        this.name = name;
        this.reason = reason;
        this.location = location;
        this.accommodationImg = accommodationImg;
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

//    public BmobFile getAccommodationImg() {
//        return accommodationImg;
//    }
//
//    public void setAccommodationImg(BmobFile accommodationImg) {
//        this.accommodationImg = accommodationImg;
//    }


    public String getAccommodationImg() {
        return accommodationImg;
    }

    public void setAccommodationImg(String accommodationImg) {
        this.accommodationImg = accommodationImg;
    }

    @Override
    public String toString() {
        return "Accommodation{" +
                "city='" + city + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", reason='" + reason + '\'' +
                ", location='" + location + '\'' +
                ", accommodationImg='" + accommodationImg + '\'' +
                '}';
    }
}
