package com.example.administrator.gaojiancheng.service;

import com.google.gson.Gson;

/**
 * 创建一个单例的gson
 * Created by Administrator on 2017/11/6.
 */

public class SingleGson {
    private static Gson gson;
    public static Gson getInstance(){
        if (gson == null){
            gson = new Gson();
        }
        return gson;
    }
}
