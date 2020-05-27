package com.gongamhada.parent;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DriveActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REFRESH_DRIVEINFO_TIME=10000;
    private static final int END_DRIVE_CODE = 1;
    String refreshURL;
    Student student;

    Button backButton;
    TextView academyName;

    TimerTask poolDriveInfoTask;
    Timer poolDriveInfoTimer;

    ContentValues contentValues;
    private GoogleMap mMap;
    private MarkerOptions driverMarkerOption;
    private MarkerOptions studentMarkerOption;
    private Marker driverMarker;
    private Marker studentMarker;

    Button driveInfoClose;
    boolean infoToggle;
    Button phoneButton;
    TextView driverName;
    TextView arriveTime;

    Bitmap busicon;
    Bitmap studentIcon;

    View driveInfoPopupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        refreshURL = getString(R.string.url) + "parent/tracking?";
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        student = (Student) intent.getSerializableExtra("student");
        contentValues = new ContentValues();
        contentValues.put("busNo", student.busID);
        contentValues.put("stdntNo",student.tracking_id);

        academyName = findViewById(R.id.academyName);
        academyName.setText(student.academyName);

        initDriveInfoView();

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(DriveActivity.this, ChoicePopupActivity.class);
                intent.putExtra("guide", "메인화면으로 이동하시겠습니까?");
                startActivityForResult(intent, END_DRIVE_CODE);
            }
        });

        busicon = BitmapFactory.decodeResource(this.getResources(), R.drawable.bus);
        studentIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.location_me);
        busicon = Bitmap.createScaledBitmap(busicon, 200, 200, false);
        studentIcon = Bitmap.createScaledBitmap(studentIcon, 75, 75, false);

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

    void initDriveInfoView(){
        driveInfoPopupView = findViewById(R.id.driveInfo);
        driveInfoPopupView.setVisibility(View.INVISIBLE);

        driveInfoClose = findViewById(R.id.driveInfoClose);
        driveInfoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoToggle = false;
                driveInfoPopupView.setVisibility(View.INVISIBLE);
            }
        });

        driverName = findViewById(R.id.driverName);
        driverName.setText(student.driverName + " 기사님");
        arriveTime = findViewById(R.id.arriveTime);

        phoneButton = findViewById(R.id.phoneButton);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+student.driverPhone)));
            }
        });

        arriveTime.setText(""+calcArriveTime());
    }
    public int calcArriveTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmm", Locale.KOREA);
        String  now = simpleDateFormat.format(System.currentTimeMillis());
        Date nowDate = null;
        Date arriveDate = null;

        try {
            nowDate = simpleDateFormat.parse(now);
            arriveDate = simpleDateFormat.parse(student.arriveTime);
            return (int)(arriveDate.getTime() - nowDate.getTime()) / 60000;

        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case END_DRIVE_CODE:
                if(resultCode == RESULT_OK) {
                    Intent intent = new Intent(DriveActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng seoul = new LatLng(37.494870, 126.960763);

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(seoul);
//        markerOptions.title("서울");
//        markerOptions.snippet("한국의 수도");
//        mMap.addMarker(markerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!infoToggle) {
                    arriveTime.setText(""+calcArriveTime());
                    driveInfoPopupView.setVisibility(View.VISIBLE);
                }
                else{
                    driveInfoPopupView.setVisibility(View.INVISIBLE);
                }
                infoToggle = !infoToggle;
                return false;
            }
        });

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
            driverMarkerOption.icon(BitmapDescriptorFactory.fromBitmap(busicon));
            driverMarker = mMap.addMarker(driverMarkerOption);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(driverMarker.getPosition()));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
        else{
            driverMarker.setPosition(new LatLng(student.driverLatitude, student.driverLongitude));
        }

        if(studentMarker == null){
            if(student.studentLatitude != 0 && student.studentLongitude != 0) {
                studentMarkerOption = new MarkerOptions();
                studentMarkerOption.position(new LatLng(student.studentLatitude, student.studentLongitude));
                studentMarkerOption.icon(BitmapDescriptorFactory.fromBitmap(studentIcon));
                studentMarker = mMap.addMarker(studentMarkerOption);
            }
        }
        else{
            studentMarker.setPosition(new LatLng(student.studentLatitude, student.studentLongitude));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(driverMarker.getPosition()));

    }

    public void parseDriveInfo(String jsonString){
        Log.d("driver_test", "drive info = " + jsonString);
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject driverInfo = jsonObject.getJSONObject("bus");
            JSONObject studentInfo = jsonObject.getJSONObject("student");

            student.driverLatitude = driverInfo.getDouble("latitude");
            student.driverLongitude = driverInfo.getDouble("longitude");
            student.studentLatitude = studentInfo.getDouble("latitude");
            student.studentLongitude = studentInfo.getDouble("longitude");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        }
        catch(Exception e){
            e.printStackTrace();

        }
        Log.d("location_test", student.driverLatitude + " , " + student.studentLatitude);
        googleMapUpdate();
    }
}
