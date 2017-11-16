package com.example.administrator.gaojiancheng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.gaojiancheng.R;
import com.example.administrator.gaojiancheng.adapter.ChatAdapter;
import com.example.administrator.gaojiancheng.enums.methodEnum;
import com.example.administrator.gaojiancheng.model.Msg;
import com.example.administrator.gaojiancheng.model.ReceiveTo;
import com.example.administrator.gaojiancheng.model.User;
import com.example.administrator.gaojiancheng.utils.ChangeMethodUtil;
import com.example.administrator.gaojiancheng.service.MessageService;
import com.example.administrator.gaojiancheng.service.SingleGson;
import com.google.gson.Gson;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class SourceActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,WebSocketManager.OnMessageListener{
    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView recyclerView;
    private ChatAdapter msgAdapter;
    private Gson gson = SingleGson.getInstance();
    private String userString;
    private Intent getIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);
        //将这个活动注册到webSocket中
        WebSocketManager.getInstance().onRegister(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //查找历史记录并显示
        try {
            msgList = searchHistory();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);//发送消息的按钮
        recyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);  //线性布局
        recyclerView.setLayoutManager(layoutManager);
        msgAdapter = new ChatAdapter(msgList);
        recyclerView.setAdapter(msgAdapter);
        //点击响应时间
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();    //得到框中的内容
                if (!"".equals(content)){
                    Msg msg = new Msg(content,Msg.TYPE_SEND);
                    msgList.add(msg);
                    //判断有新消息出现，刷新recyclerView的显示
                    msgAdapter.notifyItemInserted(msgList.size()-1);
                    //将recyclerView定位到最后一行
                    recyclerView.scrollToPosition(msgList.size()-1);
                    inputText.setText("");
                }

                //然后再将内容传送到服务器，先把要发送的东西包装好
                getIntent = getIntent();
                userString = getIntent.getStringExtra("userChat");
                try {
                    ReceiveTo<Msg> msgReceiveTo = new MessageService().messagePackage(ChangeMethodUtil.StringToT(userString, User.class),content);
                    //得到对应的webSocket并发送
                    WebSocketManager.getInstance().getWebSocket().send(ChangeMethodUtil.objectToJson(msgReceiveTo));
                    //接下来把内容存到自己的数据库中，存入之前先把状态修改为发送状态
                    //初始化数据库
                    LitePal.getDatabase();
//                    //测试先来一波清空
//                    DataSupport.deleteAll(Msg.class);
                    //将信息状态修改为发送状态并保存到数据库
                    msgReceiveTo.getRequestBody().setType(Msg.TYPE_SEND);
                    msgReceiveTo.getRequestBody().save();
                    System.out.println("保存数据成功");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.source, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            //点击查看好友资料，将Id号发送给服务器端就可以
            ReceiveTo<Integer> receiveTo = new ReceiveTo<>();
            getIntent = getIntent();
            userString = getIntent.getStringExtra("userChat");
            receiveTo.setMethod(methodEnum.FRIEND_INFORMATION.getState());
            receiveTo.setRequestBody(gson.fromJson(userString,User.class).getUserId());

            WebSocketManager.getInstance().getWebSocket().send(ChangeMethodUtil.objectToJson(receiveTo));
        } else if (id == R.id.nav_send) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMessage(String msg) {
        if (msg.startsWith(Integer.toString(methodEnum.CHAT_USER.getState()))) {
            msg = msg.substring(1);
            Msg receiveAsMsg = gson.fromJson(msg,Msg.class);
            msgList.add(receiveAsMsg);
            //判断有新消息出现，刷新recyclerView的显示
            msgAdapter.notifyItemInserted(msgList.size()-1);
            //将recyclerView定位到最后一行
            recyclerView.scrollToPosition(msgList.size()-1);
        }else if (msg.startsWith(Integer.toString(methodEnum.FRIEND_INFORMATION.getState()))){
            //保存信息，转移阵地
            Intent friendInformationActivity = new Intent(SourceActivity.this,FriendInformationActivity.class);
            friendInformationActivity.putExtra("friendInformation",msg);
            startActivity(friendInformationActivity);
        }
    }

    /**
     * 获得聊天记录
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private List<Msg> searchHistory() throws InstantiationException, IllegalAccessException {
        Intent getIntent = getIntent();
        String userString = getIntent.getStringExtra("userChat");
        //得到点击的信息账户
        User onClickUser = ChangeMethodUtil.StringToT(userString, User.class);
        //查询到和这个账户有关系的聊天消息记录
        List<Msg> msgList = DataSupport.select("*").where("toUserAccount = ? or fromUserAccount = ?",
                onClickUser.getUserAccount(),onClickUser.getUserAccount())
                .find(Msg.class);
        return msgList;
    }
}
