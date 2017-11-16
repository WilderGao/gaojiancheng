package com.example.administrator.gaojiancheng.model;

/**
 * Created by Administrator on 2017/10/28.
 */

public class Feedback<T> {
    private int state;
    private T data;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
