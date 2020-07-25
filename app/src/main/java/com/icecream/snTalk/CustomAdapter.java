package com.icecream.snTalk;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Chat> {

    public int layout;
    public class ViewHolder2 {
        TextView pf_businessTitle;
        TextView pf_addressDistance;
        TextView pf_businessAddress;
        TextView pf_businessLocation;
        TextView pf_businessState;
        TextView pf_businessZipCode;
    }

    // MyEvents myEvents;
    public CustomAdapter(Context context, int resource, List<Chat> objects) {
        super(context, resource, objects);
        layout=resource;
    }
    Context mContext;



    @Override
    public View getView(final int position, View convertView, final ViewGroup parent){

        CustomAdapter.ViewHolder2 mainViewHolder=null;
        mContext = parent.getContext();

        LayoutInflater inflater=LayoutInflater.from(getContext());
        convertView= inflater.inflate(R.layout.mylistitem,parent,false );

        final CustomAdapter.ViewHolder2 viewHolder=new CustomAdapter.ViewHolder2();
        viewHolder.pf_businessTitle =(TextView)convertView.findViewById(R.id.TextView1);
        viewHolder.pf_addressDistance =(TextView)convertView.findViewById(R.id.text_message_time);
        convertView.setTag(viewHolder);

        viewHolder.pf_businessTitle.setText(getItem(position).msg);
        viewHolder.pf_addressDistance.setText(getItem(position).time);

        return convertView;
    }
}