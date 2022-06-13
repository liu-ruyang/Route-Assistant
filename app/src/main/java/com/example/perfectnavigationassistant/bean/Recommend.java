package com.example.perfectnavigationassistant.bean;

import cn.bmob.v3.BmobObject;

public class Recommend extends BmobObject {
    private Integer sign;

    public Integer getSign() {
        return sign;
    }

    public void setSign(Integer sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "Recommend{" +
                "sign=" + sign +
                '}';
    }
}
