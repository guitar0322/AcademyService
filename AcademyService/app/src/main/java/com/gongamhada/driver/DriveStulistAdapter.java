package com.gongamhada.driver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class DriveStulistAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Student> stulist;
    DriveActivity activiy;
    public DriveStulistAdapter(Context context, ArrayList<Student> data, DriveActivity activiy){
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        stulist = data;
        this.activiy = activiy;
    }
    @Override
    public int getCount() {
        return stulist.size();
    }

    @Override
    public Object getItem(int position) {
        return stulist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.drive_stulist_layout, null);

        final TextView studentName = view.findViewById(R.id.name);
        studentName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("student_test", "student_position = " + position);
                Intent intent = new Intent(context, StudentInfoActivity.class);
                intent.putExtra("student", stulist.get(position));
                intent.putExtra("index", position);
                activiy.curStudentIdx = position;
                activiy.startActivityForResult(intent, activiy.STUDENT_INFO_CODE);
            }
        });
        studentName.setText(stulist.get(position).name);

        CheckBox checkBox = view.findViewById(R.id.checkbox);
        if(activiy.pointList.get(activiy.curPointIdx).check == true){
            checkBox.setChecked(stulist.get(position).check);
            checkBox.setClickable(false);
        }
        checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                stulist.get(position).check = isChecked;
            }
        });
        return view;
    }
}
