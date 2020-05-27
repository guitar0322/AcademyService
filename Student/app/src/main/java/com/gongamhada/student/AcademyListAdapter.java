package com.gongamhada.student;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import com.gongamhada.student.R;

public class AcademyListAdapter extends BaseAdapter {
    Context context = null;
    LayoutInflater layoutInflater = null;
    ArrayList<Academy> academyList;

    public AcademyListAdapter(Context context, ArrayList<Academy> academyList){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.academyList = academyList;
    }
    @Override
    public int getCount() {
        return academyList.size();
    }

    @Override
    public Object getItem(int position) {
        return academyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.academylist_layout, parent, false);

        TextView drivetatus = convertView.findViewById(R.id.driveStatus);
        if(academyList.get(position).status == false) {
            convertView.setBackground(ContextCompat.getDrawable(context, R.drawable.off));
            drivetatus.setText("운행완료");
        }
        else {
            convertView.setBackground(ContextCompat.getDrawable(context, R.drawable.on));
            drivetatus.setText("운행중");
        }

        TextView acaName = convertView.findViewById(R.id.academyName);
        TextView courseName = convertView.findViewById(R.id.courseName);
        TextView driverPhone = convertView.findViewById(R.id.driverPhone);

        courseName.setText(academyList.get(position).courseName);
        acaName.setText(academyList.get(position).name);
        driverPhone.setText("기사님 : " + academyList.get(position).driverPhone);

        return convertView;
    }
}
