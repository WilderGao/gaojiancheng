package com.example.administrator.gaojiancheng.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.gaojiancheng.R;
import com.example.administrator.gaojiancheng.enums.methodEnum;
import com.example.administrator.gaojiancheng.model.Feedback;
import com.example.administrator.gaojiancheng.model.Msg;
import com.example.administrator.gaojiancheng.model.User;
import com.example.administrator.gaojiancheng.service.SingleGson;
import com.example.administrator.gaojiancheng.utils.PatternUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;


public class FriendActivity extends AppCompatActivity implements WebSocketManager.OnMessageListener {
    private Button startButton;
    private EditText loginPassword;
    private EditText loginText;
    private Gson gson = SingleGson.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_chat);
        WebSocketManager.getInstance().onRegister(this);    //将这个活动注册到全局变量


        startButton = (Button) findViewById(R.id.login_button);
        loginText = (EditText) findViewById(R.id.login);
        loginPassword = (EditText) findViewById(R.id.login_password);

        WebSocketManager.getInstance().connect();

        startButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //用正则表达式判断输入是否合法
                String account = loginText.getText().toString();
                String password = loginPassword.getText().toString();
                if (PatternUtil.isEmail(account)) {
                    //把东西存到数据库
                    SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                    editor.putString("account", account);
                    editor.putString("password", password);
                    editor.apply();

                    WebSocketManager.getInstance().connect();
                }else if (password.equals("") || password.length() < 6){
                    Toast.makeText(FriendActivity.this,"密码长度不符",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(FriendActivity.this,"请输入正确格式的账号",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        WebSocketManager.getInstance().onRemove(this);
    }

    @Override
    public void onMessage(String msg) {
        System.out.println("这里都没来吗"+msg);
        if (msg.startsWith(Integer.toString(methodEnum.LOGIN.getState()))) {
            msg = msg.substring(1);
            //这里的消息包含了好友列表信息和暂存在服务器中的信息
            System.out.println("打印出的msg为"+msg);
            String[] resultMsgString = msg.split("&");

            Feedback<List<User>> feedbackUser = gson.fromJson(resultMsgString[0],new TypeToken<Feedback<List<User>>>(){}.getType());
            Feedback<List<Msg>> feedBackMsg = gson.fromJson(resultMsgString[1],new TypeToken<Feedback<List<Msg>>>(){}.getType());
            //分别得到好友列表和暂存在服务器中的信息

            if (feedbackUser.getData() != null) {
                //处理消息，将之保存到数据库
                for (Msg msg1 : feedBackMsg.getData()) {
                    System.out.println("有数据来保存");
                    //将数据保存到数据库当中
                    msg1.save();
                }

                Intent loginActivity = new Intent(FriendActivity.this, LoginActivity.class);
                loginActivity.putExtra("friendsList", resultMsgString[0]);
                startActivity(loginActivity);
                finish();
            }else {
                new AlertDialog.Builder(FriendActivity.this)
                        .setTitle("提示")
                        .setMessage("密码错误或账号不存在")
                        .setNegativeButton("确定",null).show();
            }
        }
    }
}
