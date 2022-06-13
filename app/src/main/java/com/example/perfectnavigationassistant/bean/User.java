package com.example.perfectnavigationassistant.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class User extends BmobObject {

    private String username;
    private String password;
    private String sex;
    private Integer vip;
    private Double balance;
    private String userImg;
    private BmobDate birth;//2022/6/7

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getVip() {
        return vip;
    }

    public void setVip(Integer vip) {
        this.vip = vip;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public BmobDate getBirth() {
        return birth;
    }

    public void setBirth(BmobDate birth) {
        this.birth = birth;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", vip=" + vip +
                ", balance=" + balance +
                ", userImg='" + userImg + '\'' +
                ", birth=" + birth +
                '}';
    }
}