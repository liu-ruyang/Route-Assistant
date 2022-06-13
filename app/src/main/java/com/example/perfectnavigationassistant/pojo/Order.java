package com.example.perfectnavigationassistant.pojo;

import java.io.Serializable;
import java.util.Arrays;

public class Order implements Serializable {
    private int amountOfOrders;//可能的顺序的总数
    private int bestOrder[];//最好的顺序
    private int minValue;//最小值，即最好的顺序的值

    public int getAmountOfOrders() {
        return amountOfOrders;
    }

    public void setAmountOfOrders(int amountOfOrders) {
        this.amountOfOrders = amountOfOrders;
    }

    public int[] getBestOrder() {
        return bestOrder;
    }

    public void setBestOrder(int[] bestOrder) {
        this.bestOrder = bestOrder;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public Order() {
    }

    public Order(int amountOfOrders, int[] bestOrder, int minValue) {
        this.amountOfOrders = amountOfOrders;
        this.bestOrder = bestOrder;
        this.minValue = minValue;
    }

    @Override
    public String toString() {
        return "Order{" +
                "amountOfOrders=" + amountOfOrders +
                ", minValue=" + minValue +
                ", bestOrder=" + Arrays.toString(bestOrder) +
                '}';
    }
}
