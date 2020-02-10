package guitar.academyservice;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import guitar.academyservice.ui.login.LoginActivity;

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
    Toolbar toolbar;
    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pointURL = getString(R.string.url) + "driver/point?";

        ListView courseListView = findViewById(R.id.courseList);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        if (courseList == null) {
            Log.d("main_test", "courseList is null");
            courseList = (ArrayList<Course>) intent.getSerializableExtra("course");
        }
        data = new ContentValues();

        initView();

        CourseListAdapter courseListAdapter = new CourseListAdapter(this, courseList);
        courseListView.setAdapter(courseListAdapter);
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                selectedCourse = courseList.get(position);
                data.put("drvNo", UserInfo.instance.id);
                data.put("acaNo", selectedCourse.academyID);
                data.put("routeNo", selectedCourse.id);
                RequestCourseList requestCourseList = new RequestCourseList(pointURL, data);
                requestCourseList.execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch(item.getItemId()){
            case R.id.account:
                intent = new Intent(MainActivity.this, InfoAuthActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.logout:
                intent = new Intent(MainActivity.this, ChoicePopupActivity.class);
                intent.putExtra("guide", "로그아웃 하시겠습니까?");
                startActivityForResult(intent, LOGOUT_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initView() {
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
    }

    public class RequestCourseList extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestCourseList(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            progressBar = findViewById(R.id.loading);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String result;
            HttpClient httpClient = new HttpClient();
            result = httpClient.request(url, values);
            if (result == "" || result == null) {
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
    public void onBackPressed() {
        Intent intent = new Intent(this, ChoicePopupActivity.class);
        intent.putExtra("guide", "앱을 종료하시겠습니까?");
        startActivityForResult(intent, APP_QUIT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case APP_QUIT_CODE:
                if (resultCode == RESULT_OK) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                break;
            case LOGOUT_CODE:
                if (resultCode == RESULT_OK) {
                    SharedPreferences preferences = getSharedPreferences("AutoLogin", MODE_PRIVATE);
                    ;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", "");
                    editor.putString("password", "");
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    public void parseCourseList(String result) {
        Log.d("main_test", "point result : " + result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray pointJsonArray = jsonObject.getJSONArray("point");
            ArrayList<Point> pointList = new ArrayList<>();
            for (int i = 0; i < pointJsonArray.length(); i++) {
                JSONObject point = pointJsonArray.getJSONObject(i);
                JSONArray studentJsonArray = point.getJSONArray("stdntInfo");
                ArrayList<Student> studentList = new ArrayList<>();
                for (int j = 0; j < studentJsonArray.length(); j++) {
                    JSONObject student = studentJsonArray.getJSONObject(j);
                    studentList.add(new Student(student.getInt("stdNo"), student.getString("name"), student.getString("tel"), "보호자이름", "보호자 연락처"));
                }
                pointList.add(new Point(point.getInt("stopNo"), point.getDouble("lat"), point.getDouble("lng"), point.getString("name"), studentList));
            }
            selectedCourse.pointList = pointList;
        } catch (Exception e) {
            e.printStackTrace();
            selectedCourse.pointList = new ArrayList<Point>();
            selectedCourse.pointList.add(new Point(1, 37.494935, 126.960970, "숭실대학교 후문", new ArrayList<Student>()));
            selectedCourse.pointList.add(new Point(2, 37.495427, 126.956205, "숭실대학교 정문", new ArrayList<Student>()));
            selectedCourse.pointList.add(new Point(3, 37.496053, 126.953920, "숭실대입구역", new ArrayList<Student>()));

            for(int i = 0; i < selectedCourse.pointList.size(); i++){
                selectedCourse.pointList.get(i).studentList.add(new Student(12,"김정삼", "01012345678", "김삼정", "01098765432"));
                selectedCourse.pointList.get(i).studentList.add(new Student(13,"김정사", "01012345678", "김사정", "01098765432"));
                selectedCourse.pointList.get(i).studentList.add(new Student(14,"김정오", "01012345678", "김오정", "01098765432"));
            }
        }
        //Todo
        //Setting to courseList of academy Object using result of networking.
    }

}
