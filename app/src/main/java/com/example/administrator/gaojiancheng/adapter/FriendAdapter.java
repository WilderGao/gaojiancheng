package com.example.administrator.gaojiancheng.adapter;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.gaojiancheng.R;
import com.example.administrator.gaojiancheng.model.User;

import java.util.List;

/**
 * Created by Administrator on 2017/10/29.
 */

public class FriendAdapter extends ArrayAdapter<User>{
    private int resourceId;
    private List<User> list;
    public FriendAdapter( Context context, int resource, List<User> textViewResourceId) {
        super(context, resource, textViewResourceId);
        resourceId = resource;
        list = textViewResourceId;
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        User user = getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView userView = (TextView) view.findViewById(R.id.friend_name);
        EditText editText = (EditText) view.findViewById(R.id.search);
        ImageView searchButton = (ImageView) view.findViewById(R.id.search_button);

        userView.setText(user.getUserName());

        return view;
    }

    public void onReceiveMsg(String user){

        notifyDataSetChanged();
    }
}
