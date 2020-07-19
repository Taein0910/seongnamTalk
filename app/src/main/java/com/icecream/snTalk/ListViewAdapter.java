package com.icecream.snTalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<Chat> sample;

    public ListViewAdapter(Context context, ArrayList<Chat> data) {
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return (sample == null) ? 0 : sample.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public Chat getItem(int position) {
        return sample.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.mylistitem, null);

        TextView msg = (TextView)view.findViewById(R.id.TextView1);
        TextView time = (TextView)view.findViewById(R.id.text_message_time);

        msg.setText(sample.get(position).getMsg());
        time.setText(sample.get(position).getTimestamp());

        return view;
    }
}