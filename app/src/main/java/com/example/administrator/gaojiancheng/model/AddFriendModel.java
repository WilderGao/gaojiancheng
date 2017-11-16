package com.example.administrator.gaojiancheng.model;


/**
 * Created by Administrator on 2017/11/6.
 */

public class AddFriendModel {
    private int myId;
    private String myAccount ;
    private int friendId;
    private String friendAccount;

    public int getMyId() {
        return myId;
    }

    public void setMyId(int myId) {
        this.myId = myId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getMyAccount() {
        return myAccount;
    }

    public void setMyAccount(String myAccount) {
        this.myAccount = myAccount;
    }

    public String getFriendAccount() {
        return friendAccount;
    }

    public void setFriendAccount(String friendAccount) {
        this.friendAccount = friendAccount;
    }

    @Override
    public String toString() {
        return "AddFriendModel{" +
                "myId=" + myId +
                ", myAccount='" + myAccount + '\'' +
                ", friendId=" + friendId +
                ", friendAccount='" + friendAccount + '\'' +
                '}';
    }
}
