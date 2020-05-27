package com.gongamhada.driver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CourseListAdapter extends BaseAdapter {
    Context context = null;
    LayoutInflater layoutInflater = null;
    ArrayList<Course> courseList;

    public CourseListAdapter(Context _context, ArrayList<Course> courseList){
        context = _context;
        this.courseList = courseList;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int position) {
        return courseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.courselist_layout, null);

        TextView courseName = view.findViewById(R.id.title);
        TextView acaName = view.findViewById(R.id.acaName);
        TextView acaPhone = view.findViewById(R.id.acaPhone);

        courseName.setText(courseList.get(position).name);
        acaName.setText(courseList.get(position).academyName);
        acaPhone.setText(courseList.get(position).academyPhone);

        return view;
    }
}
