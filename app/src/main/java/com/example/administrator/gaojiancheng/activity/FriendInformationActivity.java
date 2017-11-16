package com.example.administrator.gaojiancheng.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.gaojiancheng.App;
import com.example.administrator.gaojiancheng.R;
import com.example.administrator.gaojiancheng.enums.methodEnum;
import com.example.administrator.gaojiancheng.model.AddFriendModel;
import com.example.administrator.gaojiancheng.model.Feedback;
import com.example.administrator.gaojiancheng.model.Msg;
import com.example.administrator.gaojiancheng.model.ReceiveTo;
import com.example.administrator.gaojiancheng.model.User;
import com.example.administrator.gaojiancheng.utils.ChangeMethodUtil;
import com.example.administrator.gaojiancheng.utils.InformationUtil;
import com.example.administrator.gaojiancheng.service.SingleGson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.DataSupport;

/**
 * 好友信息的活动
 */
public class FriendInformationActivity extends AppCompatActivity implements WebSocketManager.OnMessageListener {
    private TextView friendNameView;
    private TextView friendAccountView;
    private TextView friendSexView;
    private TextView friendAgeView;
    private Button deleteButton;
    private Gson gson = SingleGson.getInstance();
    private User userInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_information);
        //注册到监听器
        WebSocketManager.getInstance().onRegister(this);
        //绑定四个view
        friendNameView = (TextView) findViewById(R.id.friend_name);
        friendAccountView = (TextView) findViewById(R.id.friend_account);
        friendSexView = (TextView) findViewById(R.id.friend_sex);
        friendAgeView = (TextView) findViewById(R.id.friend_age);
        deleteButton = (Button) findViewById(R.id.delete_friend);

        //得到前一个活动传来的消息
        Intent intent = getIntent();
        String msg = intent.getStringExtra("friendInformation");
        System.out.println(msg + "打印出来的msg");
        //将它转化成对应的对象
        Feedback<User> userFeedback = gson.fromJson(msg.substring(1), new TypeToken<Feedback<User>>() {
        }.getType());
        userInformation = userFeedback.getData();
        friendNameView.setText(userInformation.getUserName());
        friendAccountView.setText(userInformation.getUserAccount());
        friendAgeView.setText(Integer.toString(userInformation.getUserAge()));
        friendSexView.setText(userInformation.getUserSex());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(FriendInformationActivity.this)
                        .setTitle("提示")
                        .setMessage("确认删除该好友吗？")
                        .setPositiveButton("残忍删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //确认删除按钮之后的操作
                                String localUserAccount = InformationUtil.getLocalUser().getUserAccount();
                                ReceiveTo<AddFriendModel> deleteFriendReceive = new ReceiveTo<>();
                                AddFriendModel addFriendModel = new AddFriendModel();
                                addFriendModel.setFriendId(userInformation.getUserId());
                                addFriendModel.setFriendAccount(userInformation.getUserAccount());
                                addFriendModel.setMyAccount(localUserAccount);
                                deleteFriendReceive.setMethod(methodEnum.DELETE_FRIEND.getState());
                                deleteFriendReceive.setRequestBody(addFriendModel);
                                //清空本地数据库的聊天记录
                                DataSupport.deleteAll(Msg.class,"fromUserAccount = ? and toUserAccount = ? " +
                                        "or fromUserAccount = ? and toUserAccount = ?" ,userInformation.getUserAccount()
                                        ,localUserAccount,localUserAccount,userInformation.getUserAccount());

                                //发送消息到服务器端
                                WebSocketManager.getInstance().getWebSocket().send(ChangeMethodUtil.objectToJson(deleteFriendReceive));
                            }
                        }).setNegativeButton("再考虑一下", null)
                        .show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        WebSocketManager.getInstance().onRemove(this);
    }

    @Override
    public void onMessage(String msg) {
        if (msg.startsWith(Integer.toString(methodEnum.DELETE_FRIEND.getState()))) {
            Feedback<Integer> feedback = gson.fromJson(msg.substring(1), new TypeToken<Feedback<Integer>>() {
            }.getType());
            if (feedback.getState() == methodEnum.OK.getState()) {
                Toast.makeText(FriendInformationActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                //需要跳转回去好友列表，请求服务器重新刷新好友列表
                User user = InformationUtil.getLocalUser();
                ReceiveTo<User> receiveTo = new ReceiveTo<>();
                receiveTo.setMethod(methodEnum.LOGIN.getState());
                receiveTo.setRequestBody(user);

                WebSocketManager.getInstance().getWebSocket().send(ChangeMethodUtil.objectToJson(receiveTo));
            }
        } else if (msg.startsWith(Integer.toString(methodEnum.LOGIN.getState()))) {
            msg = msg.substring(1);
            Intent loginActivity = new Intent(FriendInformationActivity.this, LoginActivity.class);
            loginActivity.putExtra("friendsList", msg.split("&")[0]);
            //finish掉两层activity
            finish();
            for (Activity activity : App.activityList) {
                if (activity instanceof LoginActivity) {
                    activity.setIntent(loginActivity);
                }
                if (activity instanceof SourceActivity) {
                    activity.finish();
                }
            }

        }
    }
}
