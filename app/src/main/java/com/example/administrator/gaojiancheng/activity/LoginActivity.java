package com.example.administrator.gaojiancheng.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.gaojiancheng.App;
import com.example.administrator.gaojiancheng.R;
import com.example.administrator.gaojiancheng.adapter.FriendAdapter;
import com.example.administrator.gaojiancheng.enums.methodEnum;
import com.example.administrator.gaojiancheng.model.Feedback;
import com.example.administrator.gaojiancheng.model.Msg;
import com.example.administrator.gaojiancheng.model.ReceiveTo;
import com.example.administrator.gaojiancheng.model.User;
import com.example.administrator.gaojiancheng.utils.ChangeMethodUtil;
import com.example.administrator.gaojiancheng.service.SingleGson;
import com.example.administrator.gaojiancheng.utils.PatternUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements WebSocketManager.OnMessageListener{
    private static Gson gson = SingleGson.getInstance();
    private EditText searchView;
    private ImageView searchButton;
    private FloatingActionButton floatingActionButton;
    private FloatingActionButton logoutButton;
    private AutoCompleteTextView logoutView;
    private ListView friendsView;
    private List<User> friendsList;
    //显示与否的标志
    private int sign = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //将这个活动注册到全局变量中
        WebSocketManager.getInstance().onRegister(this);
        friendsView = (ListView) findViewById(R.id.list_view);
        searchView = (EditText) findViewById(R.id.search);
        searchButton = (ImageView) findViewById(R.id.search_button);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_button);
        logoutButton = (FloatingActionButton) findViewById(R.id.logout_button);
        logoutView = (AutoCompleteTextView) findViewById(R.id.logout_view);

        friendsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = friendsList.get(position);
                Intent chatActivity = new Intent(LoginActivity.this,SourceActivity.class);
                chatActivity.putExtra("userChat", ChangeMethodUtil.objectToJson(user));
                //将内容转到另一个新的activity
                startActivity(chatActivity);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = searchView.getText().toString();
                if (content.equals("")){
                    Toast.makeText(LoginActivity.this,"搜索内容不能为空",Toast.LENGTH_LONG).show();
                }else if (!PatternUtil.isEmail(content)){
                    Toast.makeText(LoginActivity.this,"输入格式有误",Toast.LENGTH_LONG).show();
                }else {
                    //将搜索的内容发送到客户端
                    ReceiveTo<String> receiveTo = new ReceiveTo<>();
                    receiveTo.setMethod(methodEnum.SEARCH_USER.getState());
                    searchView.setText("");
                    receiveTo.setRequestBody(content);
                    WebSocketManager.getInstance().getWebSocket().send(gson.toJson(receiveTo));
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sign == 0){
                    logoutButton.setVisibility(View.VISIBLE);
                    logoutButton.animate().rotation(720);
                    logoutView.setVisibility(View.VISIBLE);
                    logoutView.animate().translationX(30);
                    sign++;
                }else if (sign == 1){
                    logoutButton.animate().rotation(-720);
                    logoutButton.setVisibility(View.GONE);
                    logoutView.setVisibility(View.GONE);
                    sign--;
                }
            }
        });
        final SharedPreferences.Editor editorPreference = App.app.getSharedPreferences("user", MODE_PRIVATE).edit();

        //注销按钮
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("提示")
                        .setMessage("确定要注销吗?")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (Activity activity : App.activityList) {
                                    activity.finish();
                                }
                                //清空保存在内部的数据
                                editorPreference.clear();
                                editorPreference.apply();
                                Intent intent = new Intent(LoginActivity.this,FriendActivity.class);
                                WebSocketManager.getInstance().getWebSocket().close(1000,"logout");
                                startActivity(intent);
                            }
                        }).setNegativeButton("取消",null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //显示出需要的东西
        Intent intent = getIntent();
        String friendsListString = intent.getStringExtra("friendsList");
        //将好友列表的字符串转为对象集合
        friendsList = getFriendList(friendsListString);
        FriendAdapter adapter = new FriendAdapter(LoginActivity.this , R.layout.friend_item,friendsList);
        friendsView.setAdapter(adapter);
    }

    @Override
    public void onMessage(String msg) {
        //要在这里操作数据
        if (msg.startsWith(Integer.toString(methodEnum.CHAT_USER.getState()))){
            msg = msg.substring(1);
            Msg receiveAsMsg = new Gson().fromJson(msg,Msg.class);
            receiveAsMsg.setFromUserAccount(receiveAsMsg.getFromUser().getUserAccount());
            receiveAsMsg.setToUserAccount(receiveAsMsg.getToUser().getUserAccount());
            //然后把数据保存到数据库中
            receiveAsMsg.save();
        }else if (msg.startsWith(Integer.toString(methodEnum.SEARCH_USER.getState()))){
            String checkMsg = msg.substring(1);
            Feedback<User> feedbackFromServer = gson.fromJson(checkMsg,new TypeToken<Feedback<User>>(){}.getType());
            if (feedbackFromServer.getData() != null) {
                //跳转到搜索结果的UI，但这个活动不能死掉，要用来接收聊天消息
                Intent informationActivity = new Intent(LoginActivity.this, InformationActivity.class);
                informationActivity.putExtra("userInformation", msg);
                startActivity(informationActivity);
            }else {
                Toast.makeText(LoginActivity.this,"用户不存在",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //将字符串转换为好友集合
    public static List<User> getFriendList(String msg){
        Feedback<List<User>> friendsFeedback = gson.fromJson(msg,new TypeToken<Feedback<List<User>>>(){}.getType());
        return friendsFeedback.getData();
    }

}
