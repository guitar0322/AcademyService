package guitar.student;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

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
        View view = layoutInflater.inflate(R.layout.academylist_layout, null);

        TextView acaName = view.findViewById(R.id.academyName);
        TextView courseName = view.findViewById(R.id.courseName);
        TextView driverName = view.findViewById(R.id.driverName);
        TextView driverPhone = view.findViewById(R.id.driverPhone);

        courseName.setText(academyList.get(position).courseName);
        acaName.setText(academyList.get(position).name);
        driverName.setText(academyList.get(position).driverName);
        driverPhone.setText(academyList.get(position).driverPhone);

        return view;
    }
}
