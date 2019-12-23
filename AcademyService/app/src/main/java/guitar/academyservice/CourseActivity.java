package guitar.academyservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.skt.Tmap.TMapPoint;

import java.util.ArrayList;

public class CourseActivity extends AppCompatActivity {
    ArrayList<String> pointNameList;
    ArrayList<Point> pointList;
    Course course;
    ListAdapter pointAdapter;
    ListView pointListView;
    ContentValues data;
    String username;
    String courseURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        courseURL = getString(R.string.url) + "drivers/test?";
        Intent intent = getIntent();
        course = (Course)intent.getSerializableExtra("course");

        initPoint();
        username = getSharedPreferences("UserInfo", MODE_PRIVATE).getString("username", "");
        data = new ContentValues();
        pointListView = findViewById(R.id.pointList);
        Button driveStartButton = findViewById(R.id.driveStartButton);
        TextView courseNameView = findViewById(R.id.courseTitle);
        pointAdapter = new ListAdapter(this, pointNameList);

        courseNameView.setText(course.name);
        pointListView.setAdapter(pointAdapter);

        driveStartButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent;
                if(checkLocationServicesStatus() == false) {
                    intent = new Intent(CourseActivity.this, PopupActivity.class);
                    intent.putExtra("guide", "운행 시작을 위해선 gps 기능을 활성화 하여야 합니다.");
                    intent.putExtra("code", PopupActivity.BASIC);
                    startActivity(intent);
                }
                else{
                    data.put("username", username);
                    data.put("pointlist", pointList.toString());
                    RequestStudentList requestStudentList = new RequestStudentList(courseURL, data);
                    requestStudentList.execute();
                }
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
            parseStudentList(o.toString());
            Intent intent = new Intent(CourseActivity.this, DriveActivity.class);
            intent.putExtra("course", course);
            startActivity(intent);
            //Todo after httpNetworking.
            //ex)Intent, terminate progress, list setting
        }

    }
    public void parseStudentList(String result){
        for(int i = 0; i < pointList.size(); i++){
            pointList.get(i).studentList = new ArrayList<>();
            pointList.get(i).studentList.add(new Student("김정삼", "01012345678", "김삼정", "01098765432"));
            pointList.get(i).studentList.add(new Student("김정사", "01012345678", "김사정", "01098765432"));
            pointList.get(i).studentList.add(new Student("김정오", "01012345678", "김오정", "01098765432"));
        }
    }

    public void initPoint(){
        pointNameList = new ArrayList<String>();
        pointList = course.pointList;

        for(int i = 0; i < pointList.size(); i++){
            pointNameList.add(pointList.get(i).name);
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
