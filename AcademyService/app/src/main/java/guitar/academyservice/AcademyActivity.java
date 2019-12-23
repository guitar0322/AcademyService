package guitar.academyservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AcademyActivity extends AppCompatActivity {
    ArrayList<String> courseNameList;
    Academy academy;
    Course selectedCourse;
    ContentValues data;
    String username;
    String academyURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academy);
        academyURL = getString(R.string.url) + "drivers/test?";
        username = getSharedPreferences("UserInfo", MODE_PRIVATE).getString("username", "");
        Intent intent = getIntent();
        academy = (Academy)intent.getSerializableExtra("academy");
        initCourse();
        data = new ContentValues();

        TextView academyNameView = (TextView)findViewById(R.id.academyTitle);
        academyNameView.setText(academy.name);

        ListView driveListView = findViewById(R.id.courseList);

        ListAdapter courseAdapter = new ListAdapter(this, courseNameList);

        driveListView.setAdapter(courseAdapter);

        driveListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                data.put("username", username);
                data.put("course", academy.courseList.get(position).name);
                selectedCourse = academy.courseList.get(position);
                RequestPointList requestPointList = new RequestPointList(academyURL, data);
                requestPointList.execute();
            }
        });
    }

    public class RequestPointList extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestPointList(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String result;
            HttpClient httpClient = new HttpClient();
            result = httpClient.request(url, values);
            if(result == "" || result == null){
                result = "academy activity networking test result";
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            parsePointList(o.toString());
            Log.d("course_test", "selected course's pointlist size = " + selectedCourse.pointList.size());
            Intent intent = new Intent(AcademyActivity.this, CourseActivity.class);
            intent.putExtra("course", selectedCourse);
            startActivity(intent);

            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }

    }
    public void parsePointList(String result){
        Log.d("course_test", "selected academy name = " + selectedCourse.name);
        selectedCourse.pointList = new ArrayList<Point>();
        selectedCourse.pointList.add(new Point(37.494935, 126.960970, "숭실대학교 후문", new ArrayList<Student>()));
        selectedCourse.pointList.add(new Point(37.495427, 126.956205, "숭실대학교 정문", new ArrayList<Student>()));
        selectedCourse.pointList.add(new Point(37.496053, 126.953920, "숭실대입구역", new ArrayList<Student>()));
        //Todo
        //Setting to courseList of academy Object using result of networking.
    }

    public void initCourse(){
        courseNameList = new ArrayList<String>();

        for(int i = 0; i < academy.courseList.size(); i++){
            courseNameList.add(academy.courseList.get(i).name);
        }
    }
}
