package com.example.administrator.gaojiancheng.enums;

/**
 * Created by Administrator on 2017/10/28.
 */

public enum methodEnum {
    LOGIN(1),               //登录
    SEARCH_USER(2),          //登录账号或密码错误
    OK(200),                //正常
    CHAT_USER(3),           //聊天信息标识
    ERROR_MESSAGE(4),       //消息为空或有误
    USER_NOT_EXIT(5),       //用户不存在
    LOGINERROR(6),          //登录失败
    ADD_FRIEND(7),          //添加好友
    ADD_SUCCESS(666),       //添加成功
    ALREADY_YOUR_FRIEND(250),   //早已经是朋友了
    FRIEND_INFORMATION(8),       //查看好友信息
    DELETE_FRIEND(9);           //删除好友
    private int state;
    methodEnum(int state){
        this.state = state;
    }
    public int getState(){
        return state;
    }
}
