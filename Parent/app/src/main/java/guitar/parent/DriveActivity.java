package guitar.parent;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DriveActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REFRESH_DRIVEINFO_TIME=10000;
    private static final int END_DRIVE_CODE = 1;
    String refreshURL;
    Student student;

    TextView academyNameView;
    TextView driverNameView;
    TextView driverPhoneView;
    TextView arriveTimeView;
    Button refreshButton;


    TimerTask poolDriveInfoTask;
    Timer poolDriveInfoTimer;

    Bitmap busicon;
    ContentValues contentValues;
    private GoogleMap mMap;
    private MarkerOptions driverMarkerOption;
    private MarkerOptions studentMarkerOption;
    private Marker driverMarker;
    private Marker studentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        refreshURL = getString(R.string.url) + "parent/drive?";
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        student = (Student) intent.getSerializableExtra("student");
        initCourseInfo();
        contentValues = new ContentValues();
        contentValues.put("academy", student.academyPhone);
        contentValues.put("course", student.courseName);
        contentValues.put("student", student.phone);

        RequestRefresh requestRefresh = new RequestRefresh(refreshURL, contentValues);
        requestRefresh.execute();

        refreshButton = findViewById(R.id.refresh);
        refreshButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                RequestRefresh requestRefresh = new RequestRefresh(refreshURL, contentValues);
                requestRefresh.execute();
            }
        });

        poolDriveInfoTask = new TimerTask(){
            @Override
            public void run(){
                DriveActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RequestRefresh requestRefresh = new RequestRefresh(refreshURL, contentValues);
                        requestRefresh.execute();
                    }
                });
            }
        };
        poolDriveInfoTimer = new Timer();

        poolDriveInfoTimer.schedule(poolDriveInfoTask, 0, REFRESH_DRIVEINFO_TIME);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, ChoicePopupActivity.class);
        intent.putExtra("guide", "메인화면으로 이동하시겠습니까?");
        startActivityForResult(intent, END_DRIVE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case END_DRIVE_CODE:
                Intent intent = new Intent(DriveActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void initCourseInfo(){
        academyNameView = findViewById(R.id.academyName);
        driverNameView = findViewById(R.id.driverName);
        driverPhoneView = findViewById(R.id.driverPhone);
        arriveTimeView = findViewById(R.id.arriveTime);

        academyNameView.setText(student.academyName);
        driverNameView.setText(student.driverName);
        driverPhoneView.setText(student.driverPhone);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng seoul = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(seoul);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

    }

    public class RequestRefresh extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestRefresh(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }
        @Override
        protected  void onPreExecute(){
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            String result;
            HttpClient httpClient = new HttpClient();
            result = httpClient.request(url, values);
            if(result == "" || result == null){
                result = "drive activity networking test result";
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            parseDriveInfo(o.toString());
            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }

    }
    public void googleMapUpdate(){
        if(driverMarker == null){
            driverMarkerOption = new MarkerOptions();
            driverMarkerOption.position(new LatLng(student.driverLatitude , student.driverLongitude));
            driverMarker = mMap.addMarker(driverMarkerOption);
        }
        else{
            driverMarker.setPosition(new LatLng(student.driverLatitude, student.driverLongitude));
        }

        if(studentMarker == null){
            studentMarkerOption = new MarkerOptions();
            studentMarkerOption.position(new LatLng(student.studentLatitude, student.studentLongitude));
            studentMarker = mMap.addMarker(studentMarkerOption);
        }
        else{
            studentMarker.setPosition(new LatLng(student.studentLatitude, student.studentLongitude));
        }
    }

    public void parseDriveInfo(String jsonString){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject driverInfo = jsonObject.getJSONObject("driver");
            JSONObject studentInfo = jsonObject.getJSONObject("student");

            student.driverLatitude = driverInfo.getDouble("latitude");
            student.driverLongitude = driverInfo.getDouble("longitude");
            student.studentLatitude = studentInfo.getDouble("latitude");
            student.studentLongitude = studentInfo.getDouble("longitude");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
            student.arriveTime = simpleDateFormat.parse(driverInfo.getString("arrive"));
        }
        catch(Exception e){
            e.printStackTrace();
        }

        if(student.arriveTime != null)
            arriveTimeView.setText(student.arriveTime.toString());
        else{
            arriveTimeView.setText("10분후 도착합니다.");
        }
        googleMapUpdate();
    }
}
