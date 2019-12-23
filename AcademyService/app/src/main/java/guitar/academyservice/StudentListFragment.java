package guitar.academyservice;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentListFragment extends Fragment {
    ArrayList<String> studentList;
    int index;
    DriveActivity activity;
    public StudentListFragment(ArrayList<String> list, int index) {
        studentList = list;
        this.index = index;
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        activity = (DriveActivity)getActivity();
    }

    @Override
    public void onDetach(){
        super.onDetach();
        activity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_student_list, container, false);
        ListAdapter listAdapter = new ListAdapter(activity, studentList);

        ListView pointListView = rootView.findViewById(R.id.studentList);

        pointListView.setAdapter(listAdapter);

        Button backButton = rootView.findViewById(R.id.backButton);
        Button checkButton = rootView.findViewById(R.id.checkButton);

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                activity.changeStudentFragment();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(index != 0) {
                    if (activity.pointList.get(index - 1).check == true) {
                        activity.pointList.get(index).check = true;
                    } else{
                        activity.pointCheckError();
                    }
                }
                else{
                    activity.pointList.get(index).check = true;
                }
            }
        });
        pointListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.studentPopup(position);
            }
        });
        return rootView;
    }

}
