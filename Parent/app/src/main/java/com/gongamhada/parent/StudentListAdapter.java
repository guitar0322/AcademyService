package com.gongamhada.parent;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class StudentListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private int groupLayout;
    private int childLayout;
    private ArrayList<Student> studentList;
    private LayoutInflater inflater;
    private MainActivity mainActivity;
    public StudentListAdapter(Context context, MainActivity mainActivity, int groupLayout, int childLayout, ArrayList<Student> data){
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        studentList = data;
        this.groupLayout = groupLayout;
        this.childLayout = childLayout;
        this.mainActivity = mainActivity;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(this.groupLayout, parent, false);
        }
        convertView.setPadding(100, 0, 0, 0);
        ImageView indicator = convertView.findViewById(R.id.indicator);
        if(isExpanded){
            indicator.setImageDrawable(context.getDrawable(R.drawable.up));
        }
        else{
            indicator.setImageDrawable(context.getDrawable(R.drawable.down));
        }

        TextView studentName = convertView.findViewById(R.id.studentName);
        studentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.expandStudent(groupPosition, isExpanded);
            }
        });
        studentName.setText(studentList.get(findStudentGroupIdx(groupPosition)).name);
        final ImageButton blockButton = convertView.findViewById(R.id.block);
        if(studentList.get(findStudentGroupIdx(groupPosition)).block){
            blockButton.setImageDrawable(context.getDrawable(R.drawable.block_on));
        }
        else{
            blockButton.setImageDrawable(context.getDrawable(R.drawable.block_off));
        }
        blockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.requestBlock(findStudentGroupIdx(groupPosition), blockButton);
            }
        });
        return convertView;
    }

    public int findStudentGroupIdx(int groupPosition){
        int cnt = 0;
        int tmp = studentList.get(0).tracking_id;
        for(int i = 0; i < studentList.size(); i++){
            if(studentList.get(i).tracking_id != tmp){
                tmp = studentList.get(i).tracking_id;
                cnt++;
            }
            if(cnt == groupPosition)
                return i;
        }
        return -1;
    }

    public int findStudentChildIdx(int groupPosition, int childPosition){
        int cnt = 0;
        int tmp = studentList.get(0).tracking_id;
        for(int i = 0; i < studentList.size(); i++){
            if(studentList.get(i).tracking_id != tmp){
                tmp = studentList.get(i).tracking_id;
                cnt++;
            }
            if(cnt == groupPosition)
                return i+childPosition;
        }
        return -1;
    }
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(this.childLayout, parent, false);
        }
        if(studentList.get(findStudentChildIdx(groupPosition, childPosition)).status)
            convertView.setBackground(context.getDrawable(R.drawable.move_box));
        else
            convertView.setBackground(context.getDrawable(R.drawable.not_move_box));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openDriveInfo(findStudentChildIdx(groupPosition, childPosition));
            }
        });
        int listIdx = findStudentChildIdx(groupPosition, childPosition);
        TextView academyName = convertView.findViewById(R.id.academyName);
        academyName.setText(studentList.get(listIdx).academyName);

        TextView academyPhone = convertView.findViewById(R.id.academyPhone);
        academyPhone.setText("학원 : " + studentList.get(listIdx).academyPhone);

        TextView driverPhone = convertView.findViewById(R.id.driverPhone);
        driverPhone.setText("기사님 : " + studentList.get(listIdx).driverPhone);

        return convertView;
    }

    @Override
    public int getGroupCount() {
        int cnt = 1;
        int tmp = studentList.get(0).tracking_id;
        for(int i = 0; i < studentList.size(); i++){
            if(tmp != studentList.get(i).tracking_id){
                tmp = studentList.get(i).tracking_id;
                cnt++;
            }
        }

        return cnt;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int groupidx = findStudentGroupIdx(groupPosition);
        int tmp = studentList.get(groupidx).tracking_id;
        int cnt = 0;
        for(int i = groupidx; i < studentList.size(); i++){
            if(tmp == studentList.get(i).tracking_id){
                cnt++;
            }
            else{
                return cnt;
            }
        }
        return cnt;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return studentList.get(findStudentGroupIdx(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return studentList.get(findStudentChildIdx(groupPosition, childPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }



    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
