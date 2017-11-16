package com.example.administrator.gaojiancheng.activity;

import android.os.Handler;
import android.os.Looper;

import com.example.administrator.gaojiancheng.enums.methodEnum;
import com.example.administrator.gaojiancheng.model.ReceiveTo;
import com.example.administrator.gaojiancheng.model.User;
import com.example.administrator.gaojiancheng.utils.InformationUtil;
import com.example.administrator.gaojiancheng.service.SingleGson;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


/**
 *
 * Created by Administrator on 2017/10/29.
 */

public class WebSocketManager {
    private static WebSocketManager webSocketManager;
    private WebSocket webSocket;
    private List<OnMessageListener> listeners = new ArrayList<>();
    private Gson gson = SingleGson.getInstance();

    interface OnMessageListener{
        void onMessage(String msg);
    }

    public static WebSocketManager getInstance(){
        if (webSocketManager == null){
            webSocketManager = new WebSocketManager();
        }
        return webSocketManager;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public WebSocket getWebSocket(){
        return this.webSocket;
    }

    public void connect(){
        EchoWebSocketListener listener = new EchoWebSocketListener();
        Request request = new Request.Builder().url("ws://120.77.38.183:8080/user").build();
        OkHttpClient client = new OkHttpClient();
        client.newWebSocket(request,listener);
    }

    //注册活动监听
    public void onRegister(OnMessageListener listener){
        WebSocketManager.getInstance().listeners.add(listener);
    }

    public void onRemove(OnMessageListener listener){
        WebSocketManager.getInstance().listeners.remove(listener);
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            WebSocketManager.getInstance().setWebSocket(webSocket);
            //从数据库中取出
            User user = InformationUtil.getLocalUser();
            if (user.getUserName() != "" && user.getUserPassword() !="") {
                System.out.println(user+"来过这里");
                //整理发送格式
                ReceiveTo<User> receiveTo = new ReceiveTo<>();
                receiveTo.setMethod(methodEnum.LOGIN.getState());
                receiveTo.setRequestBody(user);

                webSocket.send(gson.toJson(receiveTo));
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, final String text) {
            System.out.println("接收到的内容为："+text);
            for (final OnMessageListener listener : WebSocketManager.getInstance().listeners) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onMessage(text);
                    }
                });

            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000,null);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {

        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        }
    }
}
