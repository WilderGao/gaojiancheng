package com.example.administrator.gaojiancheng.utils;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/10/30.
 */

public class ChangeMethodUtil {
    private static Gson gson = new Gson();
    /**
     * 将对象转为字符串
     * @param user
     * @return
     */
    public static String objectToJson(Object user){
        return gson.toJson(user);
    }

    /**
     * 将字符串转化为对象
     * @param userString
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T StringToT(String userString , Class<T> tClass) throws IllegalAccessException, InstantiationException {
        T t = tClass.newInstance();
        return gson.fromJson(userString,tClass);
    }
}
