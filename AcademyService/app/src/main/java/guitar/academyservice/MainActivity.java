package guitar.academyservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.OnLifecycleEvent;
import guitar.academyservice.ui.login.LoginActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    static final int APP_QUIT_CODE = 1;
    static final int LOGOUT_CODE = 2;

    String username;
    String pointURL;
    ContentValues data;

    ArrayList<Course> courseList;
    ArrayList<String> courseNameList;
    Course selectedCourse;

    ProgressBar progressBar;
    Button infoButton;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pointURL = getString(R.string.url) + "driver/point?";

        ListView courseListView = findViewById(R.id.courseList);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        if(courseList == null){
            Log.d("main_test", "courseList is null");
            courseList = (ArrayList<Course>)intent.getSerializableExtra("course");
            initCourseList();
        }
        data = new ContentValues();
        infoButton = findViewById(R.id.infoButton);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);

        infoButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        ListAdapter academyAdapter = new ListAdapter(this, courseNameList);

        courseListView.setAdapter(academyAdapter);
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView parent, View v, int position, long id){
                selectedCourse = courseList.get(position);
                data.put("username", username);
                data.put("course", courseList.get(position).name);
                RequestCourseList requestCourseList = new RequestCourseList(pointURL, data);
                requestCourseList.execute();
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if(id == R.id.account){
                    Intent intent = new Intent(MainActivity.this, InfoAuthActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                else if(id == R.id.setting){
                    Log.d("navigation_test", "setting selected");
                }
                else if(id == R.id.logout){
                    Intent intent = new Intent(MainActivity.this, ChoicePopupActivity.class);
                    intent.putExtra("guide", "로그아웃 하시겠습니까?");
                    startActivityForResult(intent, LOGOUT_CODE);
                }
                return false;
            }
        });
    }

    public class RequestCourseList extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestCourseList(String url, ContentValues values){
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
                result = "main activity networking test result";
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressBar.setVisibility(View.GONE);
            parseCourseList(o.toString());
            Log.d("main_test", "selected academy's courselist size = " + selectedCourse.pointList.size());
            Intent intent = new Intent(MainActivity.this, CourseActivity.class);
            intent.putExtra("course", selectedCourse);
            startActivity(intent);

            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, ChoicePopupActivity.class);
        intent.putExtra("guide", "앱을 종료하시겠습니까?");
        startActivityForResult(intent, APP_QUIT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case APP_QUIT_CODE:
                if(resultCode == RESULT_OK){
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                break;
            case LOGOUT_CODE:
                if(resultCode == RESULT_OK){
                    SharedPreferences preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", "");
                    editor.putString("password","");
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    public void parseCourseList(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray pointJsonArray = jsonObject.getJSONArray("point");
            ArrayList<Point> pointList = new ArrayList<>();
            for(int i = 0; i < pointJsonArray.length(); i++){
                JSONObject point = pointJsonArray.getJSONObject(i);
                pointList.add(new Point(-1, -1, point.getString("name"), null));
            }
            selectedCourse.pointList = pointList;
        }
        catch(Exception e){
            e.printStackTrace();
            selectedCourse.pointList = new ArrayList<Point>();
            selectedCourse.pointList.add(new Point(37.494935, 126.960970, "숭실대학교 후문", new ArrayList<Student>()));
            selectedCourse.pointList.add(new Point(37.495427, 126.956205, "숭실대학교 정문", new ArrayList<Student>()));
            selectedCourse.pointList.add(new Point(37.496053, 126.953920, "숭실대입구역", new ArrayList<Student>()));
        }
        //Todo
        //Setting to courseList of academy Object using result of networking.
    }

    public void initCourseList(){
        courseNameList = new ArrayList<>();
        for(int i = 0; i < courseList.size(); i++){
            courseNameList.add(courseList.get(i).name);
        }
    }
}
