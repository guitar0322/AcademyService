package guitar.parent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class StudentListAdapter extends BaseAdapter {
    static final int BLOCK_CODE = 3;
    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<Student> studentList;
    MainActivity activity;
    public StudentListAdapter(Context context, MainActivity activity, ArrayList<Student> data){
        this.context = context;
        this.activity = activity;
        this.studentList = data;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        if(studentList != null)
            return studentList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return studentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.studentlist_layout, null);

        View body = view.findViewById(R.id.body);
        body.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.openDriveInfo(position);
            }
        });

        TextView studentName = view.findViewById(R.id.studentName);
        TextView academyName = view.findViewById(R.id.academyName);
        TextView academyPhone = view.findViewById(R.id.academyPhone);
        TextView driverName = view.findViewById(R.id.driverName);
        TextView driverPhone = view.findViewById(R.id.driverPhone);
        final Switch blockSwitch = view.findViewById(R.id.blockSwitch);

        blockSwitch.setChecked(studentList.get(position).block);
        blockSwitch.setOnClickListener(new Switch.OnClickListener(){
            @Override
            public void onClick(View view) {
                blockSwitch.toggle();
                activity.requestBlock(position, blockSwitch);
            }
        });

        studentName.setText(studentList.get(position).name);
        academyName.setText(studentList.get(position).academyName);
        academyPhone.setText(studentList.get(position).academyPhone);
        driverName.setText(studentList.get(position).driverName);
        driverPhone.setText(studentList.get(position).driverPhone);

        return view;
    }
}
