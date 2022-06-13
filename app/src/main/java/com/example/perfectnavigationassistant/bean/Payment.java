package com.example.perfectnavigationassistant.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class Payment extends BmobObject {

    private User userId;
    private BmobDate payTime;
    private Double amount;

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public BmobDate getPayTime() {
        return payTime;
    }

    public void setPayTime(BmobDate payTime) {
        this.payTime = payTime;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "userId=" + userId +
                ", payTime=" + payTime +
                ", amount=" + amount +
                '}';
    }
}
