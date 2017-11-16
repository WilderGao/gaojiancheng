package com.example.administrator.gaojiancheng.model;

import android.support.annotation.NonNull;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/10/28.
 */

public class User extends DataSupport implements Comparable<User>{
    private int userId;
    private String userAccount;
    private String userName ;
    private String userPassword;
    private int userAge;
    private String userSex;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }


    @Override
    public int compareTo(@NonNull User o) {
        return this.getUserName().compareTo(o.getUserName());
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userAccount='" + userAccount + '\'' +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userAge=" + userAge +
                ", userSex='" + userSex + '\'' +
                '}';
    }
}
