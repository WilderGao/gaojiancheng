package com.example.administrator.gaojiancheng.model;

import org.litepal.crud.DataSupport;

/**
 * 消息类，向服务器传送数据和将数据保存到数据库的类
 * Created by Administrator on 2017/10/29.
 */

public class Msg extends DataSupport{
    public static final int TYPE_RECEIVED = 0 ;
    public static final int TYPE_SEND = 1 ;
    private String content ;
    private int type ;
    private User fromUser;      //发送者
    private User toUser;    //接收者
    private String fromUserAccount;
    private String toUserAccount;

    public Msg(String content , int type){
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User getFromUser() {

        return this.fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
        this.fromUserAccount = fromUser.getUserAccount();
    }

    public User getToUser() {
        return this.toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
        this.toUserAccount = toUser.getUserAccount();
    }

    public String getFromUserAccount() {
        return fromUserAccount;
    }

    public void setFromUserAccount(String fromUserAccount) {
        this.fromUserAccount = fromUserAccount;
    }

    public String getToUserAccount() {
        return toUserAccount;
    }

    public void setToUserAccount(String toUserAccount) {
        this.toUserAccount = toUserAccount;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "content='" + content + '\'' +
                ", type=" + type +
                ", fromUser=" + fromUser +
                ", toUser=" + toUser +
                ", fromUserAccount='" + fromUserAccount + '\'' +
                ", toUserAccount='" + toUserAccount + '\'' +
                '}';
    }
}
