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
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentListFragment extends Fragment {
    private static final int CHECK_POINT_CODE = 1;
    ArrayList<Student> studentList;
    DriveActivity activity;
    public StudentListFragment(ArrayList<Student> list) {
        studentList = list;
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_student_list, container, false);

        final DriveStulistAdapter stulistAdapter = new DriveStulistAdapter(activity, studentList, activity);
        ListView studentListView = rootView.findViewById(R.id.studentList);
        studentListView.setAdapter(stulistAdapter);

        TextView pointName = rootView.findViewById(R.id.pointName);
        pointName.setText(activity.pointList.get(activity.curPointIdx).name);
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
                if(activity.pointList.get(activity.curPointIdx).check == true){
                    Intent intent1 = new Intent(activity, PopupActivity.class);
                    intent1.putExtra("guide", "이미 체크되었습니다");
                    startActivity(intent1);
                    return;
                }
                else{
                    Intent intent = new Intent(activity, ChoicePopupActivity.class);
                    intent.putExtra("guide", "체크완료 하시겠습니까?");
                    startActivityForResult(intent, CHECK_POINT_CODE);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHECK_POINT_CODE:
                if(resultCode == RESULT_OK){
                    Log.d("_test", "result_ok");
                    if(activity.curPointIdx == 0){
                        activity.requestCheckPoint(activity.curPointIdx);
                    }
                    else if (activity.pointList.get(activity.curPointIdx - 1).check == true) {
                        activity.requestCheckPoint(activity.curPointIdx);
                    }
                    else{
                        activity.pointCheckError();
                    }
                }
                break;
        }
    }
}
