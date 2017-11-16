package com.example.administrator.gaojiancheng.service;

import android.content.SharedPreferences;

import com.example.administrator.gaojiancheng.App;
import com.example.administrator.gaojiancheng.model.Msg;
import com.example.administrator.gaojiancheng.model.ReceiveTo;
import com.example.administrator.gaojiancheng.model.User;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2017/10/30.
 */

public class MessageService  {
    private static final int USER_CHAT = 3; //在服务器端3代表聊天
    /**
     * 把要发送到其它客户端的信息都打包
     * @param toUser 收信人
     * @param content 发送内容
     * @return  发送模板
     */
    public ReceiveTo<Msg> messagePackage(User toUser , String content){
        //创建一个发送过去的消息类
        ReceiveTo<Msg> msgReceiveTo = new ReceiveTo<>();
        //首先从sharePreference中拿到本账号的消息
        SharedPreferences preferences = App.app.getSharedPreferences("user", MODE_PRIVATE);
        //得到当前信息的账号
        String account = preferences.getString("account","");
        //将账号封装在User类中
        User fromUser = new User();
        fromUser.setUserAccount(account);

        //对于对方来说这是接收到的信息
        Msg msg = new Msg(content,Msg.TYPE_RECEIVED);
        msg.setToUser(toUser);
        msg.setFromUser(fromUser);
        msgReceiveTo.setRequestBody(msg);
        msgReceiveTo.setMethod(USER_CHAT);

        return msgReceiveTo;

    }
}
