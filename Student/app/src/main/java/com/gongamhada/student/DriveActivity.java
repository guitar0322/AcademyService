package com.gongamhada.student;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import com.gongamhada.student.R;

public class DriveActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REFRESH_DRIVEINFO_TIME=10000;
    private static final int END_DRIVE_CODE = 1;
    private static final int SEND_LOCATION_TASK = 10;
    private static final int POOL_DRIVE_INFO_TASK = 20;
    String trackingURL;
    String locationURL;
    Academy academy;

    GPSManager gpsManager;
    TimerTask poolDriveInfoTask;
    Timer poolDriveInfoTimer;

    TextView academyNameView;
    Button backButton;

    ContentValues contentValues;
    private GoogleMap mMap;
    private MarkerOptions driverMarkerOption;
    private MarkerOptions myMarkerOption;
    private Marker driverMarker;
    private Marker myMarker;

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

        trackingURL = getString(R.string.url) + "stdnt/tracking?";
        locationURL = getString(R.string.url) + "stdnt/location?";

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        academy = (Academy) intent.getSerializableExtra("academy");
        academyNameView = findViewById(R.id.academyName);
        academyNameView.setText(academy.name);

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

        contentValues = new ContentValues();
        contentValues.put("busNo", academy.busID);

        gpsManager = new GPSManager(this);

        NetworkTask requestInitDriveInfo = new NetworkTask(trackingURL, contentValues, POOL_DRIVE_INFO_TASK);
        requestInitDriveInfo.execute();

        poolDriveInfoTask = new TimerTask(){
            @Override
            public void run(){
                DriveActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gpsManager.getLocation();
                        ContentValues myLocationValues = new ContentValues();
                        myLocationValues.put("stdntNo", UserInfo.instance.id);
                        myLocationValues.put("latitude", gpsManager.getLatitude());
                        myLocationValues.put("longitude", gpsManager.getLongitude());

                        NetworkTask poolDriveInfo = new NetworkTask(trackingURL, contentValues, POOL_DRIVE_INFO_TASK);
                        poolDriveInfo.execute();
                        NetworkTask sendLocation = new NetworkTask(locationURL, myLocationValues, SEND_LOCATION_TASK);
                        sendLocation.execute();
                    }
                });
            }
        };
        poolDriveInfoTimer = new Timer();

        poolDriveInfoTimer.schedule(poolDriveInfoTask, 0, REFRESH_DRIVEINFO_TIME);
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
        driverName.setText(academy.driverName + " 기사님");
        arriveTime = findViewById(R.id.arriveTime);

        phoneButton = findViewById(R.id.phoneButton);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("android.intent.action.CALL", Uri.parse(academy.driverPhone)));
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
            arriveDate = simpleDateFormat.parse(academy.arriveTime);
            return (int)(arriveDate.getTime() - nowDate.getTime()) / 60000;

        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, ChoicePopupActivity.class);
        intent.putExtra("guide", "메인화면으로 이동하시겠습니까?");
        startActivityForResult(intent, END_DRIVE_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        poolDriveInfoTimer.cancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case END_DRIVE_CODE:
                if(resultCode == RESULT_OK) {
                    poolDriveInfoTimer.cancel();
                    Intent intent = new Intent(DriveActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng seoul = new LatLng(37.56, 126.97);
//        LatLng seoul = pathPointList.get(50);

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(seoul);
//        markerOptions.title("서울");
//        markerOptions.snippet("한국의 수도");
//        mMap.addMarker(markerOptions);
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

//        View infoWindow = getLayoutInflater().inflate(R.layout.activity_drive_info, null);
//        DriverInfoAdapter driverInfoAdapter = new DriverInfoAdapter(infoWindow, academy);
//        mMap.setInfoWindowAdapter(driverInfoAdapter);

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
//        Polyline polyline = mMap.addPolyline((new PolylineOptions()).clickable(true).addAll(pathPointList));
//        polyline.setTag("firstpath");
    }

    public class NetworkTask extends AsyncTask {
        private String url;
        private ContentValues values;
        int task;

        public NetworkTask(String url, ContentValues values, int taskCode) {
            this.url = url;
            this.values = values;
            this.task = taskCode;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String result;
            HttpClient httpClient = new HttpClient();
            result = httpClient.request(url, values);
            if (result == "" || result == null) {
                result = "student drive activity networking error";
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            switch (task){
                case SEND_LOCATION_TASK:
                    break;
                case POOL_DRIVE_INFO_TASK:
                    parseDriveInfo(o.toString());
                    break;
            }
            //Todo after httpNetworking.
            //ex)Intent, terminate progress, list setting
        }
    }

    public void googleMapUpdate(){
        Log.d("gps_test", academy.driverLatitude + " , " + academy.driverLongitude);
        Log.d("gps_test", gpsManager.getLatitude() + " , " + gpsManager.getLongitude());
        if(driverMarker == null){
            driverMarkerOption = new MarkerOptions();
            driverMarkerOption.position(new LatLng(academy.driverLatitude , academy.driverLongitude));
            driverMarkerOption.icon(BitmapDescriptorFactory.fromBitmap(busicon));
            driverMarker = mMap.addMarker(driverMarkerOption);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(driverMarker.getPosition()));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
        else{
            driverMarker.setPosition(new LatLng(academy.driverLatitude, academy.driverLongitude));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(driverMarker.getPosition()));
        }

        if(myMarker == null){
            myMarkerOption = new MarkerOptions();
            myMarkerOption.position(new LatLng(gpsManager.getLatitude(), gpsManager.getLongitude()));
            myMarkerOption.icon(BitmapDescriptorFactory.fromBitmap(studentIcon));
            myMarker = mMap.addMarker(myMarkerOption);
        }
        else{
            myMarker.setPosition(new LatLng(gpsManager.getLatitude(), gpsManager.getLongitude()));
        }
    }

    public void parseDriveInfo(String jsonString){
        try {
            JSONObject driverInfo = new JSONObject(jsonString);

            academy.driverLatitude = driverInfo.getDouble("latitude");
            academy.driverLongitude = driverInfo.getDouble("longitude");
            googleMapUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
