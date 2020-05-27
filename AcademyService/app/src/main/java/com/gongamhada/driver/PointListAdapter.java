package com.gongamhada.driver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PointListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private int groupLayout = 0;
    private int childLayout = 0;
    private ArrayList<Point> pointList;
    private LayoutInflater myinf = null;

    public PointListAdapter(Context context,int groupLay,int childLayout,ArrayList<Point> DataList){
        this.pointList = DataList;
        this.groupLayout = groupLay;
        this.childLayout = childLayout;
        this.context = context;
        this.myinf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if(convertView == null){
            convertView = myinf.inflate(this.groupLayout, parent, false);
        }
        ImageView indicator = convertView.findViewById(R.id.indicator);

        if(isExpanded){
            indicator.setImageDrawable(context.getDrawable(R.drawable.up));
        }
        else{
            indicator.setImageDrawable(context.getDrawable(R.drawable.down));
        }

        TextView groupName = (TextView)convertView.findViewById(R.id.title);
        groupName.setText(pointList.get(groupPosition).name);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if(convertView == null){
            convertView = myinf.inflate(this.childLayout, parent, false);
        }
        convertView.setPadding(35,0,0,0);
        TextView childName = (TextView)convertView.findViewById(R.id.name);
        childName.setText(pointList.get(groupPosition).studentList.get(childPosition).name);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return pointList.get(groupPosition).studentList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return pointList.get(groupPosition).studentList.size();
    }

    @Override
    public Point getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return pointList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return pointList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

}
