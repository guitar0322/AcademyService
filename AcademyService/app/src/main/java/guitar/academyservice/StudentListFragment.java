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

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentListFragment extends Fragment {
    private static final int CHECK_POINT_CODE = 1;
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
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
                if(activity.pointList.get(index).check == true){
                    Intent intent1 = new Intent(activity, PopupActivity.class);
                    intent1.putExtra("guide", "이미 체크된 경유지입니다");
                    startActivity(intent1);
                    return;
                }
                else{
                    Intent intent = new Intent(activity, ChoicePopupActivity.class);
                    intent.putExtra("guide", "경유지 체크를 하시겠습니까?");
                    startActivityForResult(intent, CHECK_POINT_CODE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHECK_POINT_CODE:
                if(resultCode == RESULT_OK){
                    Log.d("_test", "result_ok");
                    if(index == 0){
                        activity.requestCheckPoint(index);
                    }
                    else if (activity.pointList.get(index - 1).check == true) {
                        activity.requestCheckPoint(index);
                    }
                    else{
                        activity.pointCheckError();
                    }
                }
                break;
        }
    }
}
