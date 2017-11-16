package com.example.administrator.gaojiancheng.activity;

import android.app.Activity;
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
import com.example.administrator.gaojiancheng.model.ReceiveTo;
import com.example.administrator.gaojiancheng.model.User;
import com.example.administrator.gaojiancheng.utils.ChangeMethodUtil;
import com.example.administrator.gaojiancheng.utils.InformationUtil;
import com.example.administrator.gaojiancheng.service.SingleGson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class InformationActivity extends AppCompatActivity implements WebSocketManager.OnMessageListener{
    private TextView nameView;
    private TextView accountView;
    private TextView sexView;
    private TextView ageView;
    private Button addFriendButton;
    private User searchUser;
    private Gson gson = SingleGson.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        //先将这个活动注册到webSocketManager的监听器中
        WebSocketManager.getInstance().onRegister(this);
        nameView = (TextView) findViewById(R.id.search_name);
        accountView = (TextView) findViewById(R.id.search_account);
        sexView = (TextView) findViewById(R.id.search_sex);
        ageView = (TextView) findViewById(R.id.search_age);
        addFriendButton = (Button) findViewById(R.id.add_friend);
        Intent getUserInformation = getIntent();
        String msg = getUserInformation.getStringExtra("userInformation");

        if (msg.startsWith(Integer.toString(methodEnum.SEARCH_USER.getState()))){
            msg = msg.substring(1);
            Feedback<User> friendFeedBack = gson.fromJson(msg,new TypeToken<Feedback<User>>(){}.getType());
            searchUser = friendFeedBack.getData();
            nameView.setText(searchUser.getUserName());
            accountView.setText(searchUser.getUserAccount());
            sexView.setText(searchUser.getUserSex());
            ageView.setText(Integer.toString(searchUser.getUserAge()));
        }

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriendModel friendModel = new AddFriendModel();
                ReceiveTo<AddFriendModel> receiveTo = new ReceiveTo<>();
                User myUser = InformationUtil.getLocalUser();
                friendModel.setFriendId(searchUser.getUserId());
                friendModel.setMyAccount(myUser.getUserAccount());
                friendModel.setFriendAccount(searchUser.getUserAccount());
                receiveTo.setMethod(methodEnum.ADD_FRIEND.getState());
                receiveTo.setRequestBody(friendModel);
                WebSocketManager.getInstance().getWebSocket().send(gson.toJson(receiveTo));
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
        //只处理有关用户信息的内容
        if (msg.startsWith(Integer.toString(methodEnum.ADD_FRIEND.getState()))){
            Feedback<Integer> feedback = gson.fromJson(msg.substring(1),new TypeToken<Feedback<Integer>>(){}.getType());
            if (feedback.getState() == methodEnum.ALREADY_YOUR_FRIEND.getState()){
                Toast.makeText(InformationActivity.this,"他已经是您的好友啦",Toast.LENGTH_LONG).show();
            }else if (feedback.getState() == methodEnum.ADD_SUCCESS.getState()){
                Toast.makeText(InformationActivity.this,"添加成功",Toast.LENGTH_LONG).show();

                User user = InformationUtil.getLocalUser();
                ReceiveTo<User> receiveTo = new ReceiveTo<>();
                receiveTo.setMethod(methodEnum.LOGIN.getState());
                receiveTo.setRequestBody(user);
                WebSocketManager.getInstance().getWebSocket().send(ChangeMethodUtil.objectToJson(receiveTo));
            }
        }if (msg.startsWith(Integer.toString(methodEnum.LOGIN.getState()))){
            Intent loginActivity = new Intent(InformationActivity.this,LoginActivity.class);
            msg = msg.substring(1);
            loginActivity.putExtra("friendsList", msg.split("&")[0]);
            for (Activity activity : App.activityList) {
                if (activity instanceof  LoginActivity){
                    activity.setIntent(loginActivity);
                }
            }
            finish();
        }
    }
}
