package com.gongamhada.driver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    Context context = null;
    LayoutInflater layoutInflater = null;
    ArrayList<String> list;

    public ListAdapter(Context _context, ArrayList<String> list){
        context = _context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.drive_plist_layout, null);

        TextView courseName = convertView.findViewById(R.id.name);

        courseName.setText(list.get(position));

        return convertView;
    }
}
