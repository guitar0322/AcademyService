package com.gongamhada.driver;

import android.content.ContentValues;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CourseActivity extends AppCompatActivity {
    ArrayList<Point> pointList;
    Course course;
    PointListAdapter pointListAdapter;
    ExpandableListView pointListView;
    TextView courseTitle;
    Toolbar toolbar;
    ActionBar actionBar;
    Button driveStartButton;
    Button backButton;
    ProgressBar progressBar;
    ContentValues data;
    String username;
    String courseURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        courseURL = getString(R.string.url) + "driver/start?";
        Intent intent = getIntent();

        course = (Course)intent.getSerializableExtra("course");
        courseTitle = findViewById(R.id.courseName);
        courseTitle.setText(course.name);
        pointList = course.pointList;
        username = getSharedPreferences("UserInfo", MODE_PRIVATE).getString("username", "");
        driveStartButton = findViewById(R.id.driveStartButton);
        backButton = findViewById(R.id.backButton);
        data = new ContentValues();

        pointListView = findViewById(R.id.pointList);
        pointListAdapter = new PointListAdapter(this, R.layout.list_layout, R.layout.studentlist_layout, pointList);
        pointListView.setAdapter(pointListAdapter);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);


        driveStartButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent;
                if(checkLocationServicesStatus() == false) {
                    intent = new Intent(CourseActivity.this, PopupActivity.class);
                    intent.putExtra("guide", "운행 시작을 위해선 gps 기능을 활성화 하여야 합니다.");
                    startActivity(intent);
                }
                else{
                    data.put("drvNo", UserInfo.instance.id);
                    data.put("acaNo", course.academyID);
                    data.put("busNo",course.busID);
                    data.put("routeNo", course.id);
                    RequestStudentList requestStudentList = new RequestStudentList(courseURL, data);
                    requestStudentList.execute();
                }
            }
        });

        backButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(CourseActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public class RequestStudentList extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestStudentList(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }

        @Override
        protected  void onPreExecute(){
            progressBar = findViewById(R.id.loading);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String result;
            HttpClient httpClient = new HttpClient();
            result = httpClient.request(url, values);
            if(result == "" || result == null){
                result = "course activity networking test result";
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            parseDriveInfo(o.toString());
            progressBar.setVisibility(View.GONE);
            //Todo after httpNetworking.
            //ex)Intent, terminate progress, list setting
        }

    }
    public void parseDriveInfo(String result){
        Log.d("course_test", "start result : " + result);
        try{
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("status").equals("ACCEPT")) {
                JSONArray pathJsonArray = jsonObject.getJSONArray("path");

                for (int i = 0; i < pathJsonArray.length(); i++) {
                    JSONObject path = pathJsonArray.getJSONObject(i);
                    course.addPath(path.getDouble("latitude"), path.getDouble("longitude"));
                }

                Intent intent = new Intent(CourseActivity.this, DriveActivity.class);
                intent.putExtra("course", course);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(CourseActivity.this, PopupActivity.class);
                intent.putExtra("guide", "운행정보를 불러오는중 오류가 발생하였습니다.");
                startActivity(intent);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            Intent intent = new Intent(CourseActivity.this, PopupActivity.class);
            intent.putExtra("guide", "네트워크 오류가 발생하였습니다. 인터넷을 확인 해주세요");
            startActivity(intent);
        }
    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
