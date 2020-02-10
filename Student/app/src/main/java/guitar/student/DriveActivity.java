package guitar.student;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

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
    TextView driverNameView;
    TextView driverPhoneView;
    TextView arriveTimeView;
    Button refreshButton;

    Bitmap busicon;

    ContentValues contentValues;
    private GoogleMap mMap;
    private MarkerOptions driverMarkerOption;
    private Marker driverMarker;

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

        if(academy.arriveTime == null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
            try {
                academy.arriveTime = simpleDateFormat.parse("2020-02-03 11:00:00");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        contentValues = new ContentValues();
        contentValues.put("busNo", academy.busID);

        gpsManager = new GPSManager(this);

        NetworkTask requestInitDriveInfo = new NetworkTask(trackingURL, contentValues, POOL_DRIVE_INFO_TASK);
        requestInitDriveInfo.execute();

        refreshButton = findViewById(R.id.refresh);
        refreshButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                NetworkTask requestRefresh = new NetworkTask(trackingURL, contentValues, POOL_DRIVE_INFO_TASK);
                requestRefresh.execute();
            }
        });

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
                poolDriveInfoTimer.cancel();
                Intent intent = new Intent(DriveActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng seoul = new LatLng(37.56, 126.97);
//        LatLng seoul = pathPointList.get(50);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(seoul);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        View infoWindow = getLayoutInflater().inflate(R.layout.activity_drive_info, null);
        DriverInfoAdapter driverInfoAdapter = new DriverInfoAdapter(infoWindow, academy);
        mMap.setInfoWindowAdapter(driverInfoAdapter);
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
        if(driverMarker == null){
            driverMarkerOption = new MarkerOptions();
            driverMarkerOption.position(new LatLng(academy.driverLatitude , academy.driverLongitude));
            driverMarker = mMap.addMarker(driverMarkerOption);
        }
        else{
            driverMarker.setPosition(new LatLng(academy.driverLatitude, academy.driverLongitude));
        }
    }

    public void parseDriveInfo(String jsonString){
        try {
            JSONObject driverInfo = new JSONObject(jsonString);

            academy.driverLatitude = driverInfo.getDouble("x");
            academy.driverLongitude = driverInfo.getDouble("y");
            googleMapUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
