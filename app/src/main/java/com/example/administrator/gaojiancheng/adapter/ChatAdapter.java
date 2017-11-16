package com.example.administrator.gaojiancheng.adapter;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.gaojiancheng.R;
import com.example.administrator.gaojiancheng.model.Msg;

import java.util.List;

/**
 * Created by Administrator on 2017/10/29.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Msg> msgList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        //创建两个线性布局
        ConstraintLayout leftLayout;
        ConstraintLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        public ViewHolder(View itemView) {
            super(itemView);
            leftLayout = (ConstraintLayout) itemView.findViewById(R.id.left_layout);
            rightLayout = (ConstraintLayout) itemView.findViewById(R.id.right_layout);
            leftMsg = (TextView) itemView.findViewById(R.id.left_msg);
            rightMsg = (TextView) itemView.findViewById(R.id.right_msg);
        }
    }

    public ChatAdapter(List<Msg> msgList){
        this.msgList = msgList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_chat,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = msgList.get(position);
        //判断是接收消息还是发送消息
        if (msg.getType() == Msg.TYPE_RECEIVED){
            //接收消息则左侧聊天框可见
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        }else if (msg.getType() == Msg.TYPE_SEND){
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }
}
