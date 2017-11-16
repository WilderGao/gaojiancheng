package com.example.administrator.gaojiancheng.utils;

import android.content.SharedPreferences;

import com.example.administrator.gaojiancheng.App;
import com.example.administrator.gaojiancheng.model.User;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Administrator on 2017/11/6.
 */

public class InformationUtil {
    /**
     * 获得登录人的账号
     * @return
     */
    public  static User getLocalUser(){
        SharedPreferences preferences = App.app.getSharedPreferences("user",MODE_PRIVATE);
        User user = new User();
        user.setUserAccount(preferences.getString("account",""));
        user.setUserPassword(preferences.getString("password",""));
        return user;
    }
}
