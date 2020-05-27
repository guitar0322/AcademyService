package com.gongamhada.driver;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PointListFragment extends Fragment {
    ArrayList<String> pointTitleList;
    DriveActivity activity;
    public PointListFragment(ArrayList<String> list) {
        pointTitleList = list;
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
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_point_list, container, false);
        ListAdapter listAdapter = new ListAdapter(activity, pointTitleList);
        ListView pointListView = rootView.findViewById(R.id.pointList);
        pointListView.setAdapter(listAdapter);
        pointListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.chanceFragment(position);
            }
        });

        return rootView;
    }
}
