package guitar.academyservice;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
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
import com.skt.Tmap.TMapPoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DriveActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final int END_DRIVE_CODE = 1;
    public static final int STUDENT_INFO_CODE = 2;
    public static final int END_DIRVE_TASK = 10;
    public static final int SEND_LOCATION_TASK = 20;
    public static final int POOL_STUDENT_LOCATION_TASK = 30;
    public static final int CHECK_POINT_TASK = 40;

    Course course;
    String studentURL;
    String checkpointURL;
    String locationURL;
    String endDriveURL;
    String username;
    ArrayList<Point> pointList;
    ArrayList<Student> studentList;
    ArrayList<TMapPoint> wayPoints;
    ArrayList<String> pointNameList;
    ArrayList<String> studentNameList;

    TMapPoint startPoint;
    TMapUtil tMapUtil;
    Bitmap busicon;

    private GoogleMap mMap;
    private ArrayList<LatLng> pathPointList;
    MarkerOptions studentMarkerOption;
    MarkerOptions locationMarkerOption;
    Marker studentMarker;
    Marker myLocationMarker;
    ProgressBar progressBar;
    Button drawerButton;
    Button backButton;
    TextView courseTitle;
    View listFrame;
    PointListFragment pointListFragment;
    StudentListFragment studentListFragment;

    GPSManager gpsManager;
    private static final int POOL_GPS_TIME = 3000;
    Timer poolGPSLocationTimer;
    private static final int SEND_LOCATION_TIME = 10000;
    Timer sendMyLocationTimer;
    int curPointIdx;
    int curStudentIdx;
    int checkStudentNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        username = getSharedPreferences("UserInfo", MODE_PRIVATE).getString("username", "");
        studentURL = getString(R.string.url) + "driver/stdnt?";
        checkpointURL = getString(R.string.url) + "driver/checkpoint?";
        locationURL = getString(R.string.url) + "driver/location?";
        endDriveURL = getString(R.string.url) + "driver/endDrive?";

        tMapUtil = new TMapUtil(this);
        busicon = BitmapFactory.decodeResource(this.getResources(), R.drawable.busicon);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = true;
                for (int i = 0; i < pointList.size(); i++) {
                    if (pointList.get(i).check == false) {
                        check = false;
                    }
                }
                if (check == true) {
                    endDrive();
                } else {
                    endDriveError();
                }
            }
        });

        drawerButton = findViewById(R.id.drawer);
        listFrame = findViewById(R.id.list_frame);
        class DrawerClickListener implements Button.OnClickListener {
            boolean toggle;

            @Override
            public void onClick(View v) {
                Log.d("draw_test", "flag = " + toggle);
                Display display = getWindowManager().getDefaultDisplay();
                android.graphics.Point size = new android.graphics.Point();
                display.getSize(size);
                if (toggle == false) {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(listFrame, "translationY", -size.y*0.57f);
                    anim.setDuration(500);
                    anim.start();
                } else {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(listFrame, "translationY", 0);
                    anim.setDuration(500);
                    anim.start();
                }
                toggle = !toggle;
            }
        }
        drawerButton.setOnClickListener(new DrawerClickListener());

        gpsManager = new GPSManager(this);
        TimerTask poolGPSLocationTask = new TimerTask() {
            @Override
            public void run() {
                DriveActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gpsManager.getLocation();
                        if (mMap != null) {
                            if (myLocationMarker == null) {
                                locationMarkerOption = new MarkerOptions();
                                locationMarkerOption.position(new LatLng(gpsManager.getLatitude(), gpsManager.getLongitude()));
                                myLocationMarker = mMap.addMarker(locationMarkerOption);
                            } else {
                                myLocationMarker.setPosition(new LatLng(gpsManager.getLatitude(), gpsManager.getLongitude()));
                                Log.d("location_test", "location = " + gpsManager.getLatitude() + " , " + gpsManager.getLongitude());
                            }
                        }
//                        Toast.makeText(DriveActivity.this, gpsManager.getLatitude() + "," + gpsManager.getLongitude(), Toast.LENGTH_SHORT).show();순서에 따라
                    }
                });
            }
        };
        poolGPSLocationTimer = new Timer();

        TimerTask sendMyLocationTask = new TimerTask() {
            @Override
            public void run() {
                ContentValues contentValues = new ContentValues();
                contentValues.put("busNo", course.busID);
                contentValues.put("latitude", gpsManager.getLatitude());
                contentValues.put("longitude", gpsManager.getLongitude());
                NetworkTask sendLocationTask = new NetworkTask(locationURL, contentValues, SEND_LOCATION_TASK);
                sendLocationTask.execute();
            }
        };

        sendMyLocationTimer = new Timer();

        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra("course");
        courseTitle = findViewById(R.id.courseName);
        courseTitle.setText(course.name);
        initPointList();
        pointListFragment = new PointListFragment(pointNameList);

        poolGPSLocationTimer.schedule(poolGPSLocationTask, 0, POOL_GPS_TIME); //get gps location and make bus icon every POOL_GPS_TIME seconds.
        sendMyLocationTimer.schedule(sendMyLocationTask, 0, SEND_LOCATION_TIME);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, pointListFragment).commit();
    }

    @Override
    public void onBackPressed() {
        boolean check = true;
        for (int i = 0; i < pointList.size(); i++) {
            if (pointList.get(i).check == false) {
                check = false;
            }
        }
        if (check == true) {
            endDrive();
        } else {
            endDriveError();
        }
    }

    public void studentPopup(int index) {
        Intent intent = new Intent(DriveActivity.this, StudentInfoActivity.class);
        intent.putExtra("student", studentList.get(index));
        startActivityForResult(intent, STUDENT_INFO_CODE);
    }

    public void requestStudentLocation() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("stdntNo", studentList.get(curStudentIdx).id);
        NetworkTask requestStudentLocation = new NetworkTask(studentURL, contentValues, POOL_STUDENT_LOCATION_TASK);
        requestStudentLocation.execute();
    }

    public void requestCheckPoint(int index) {
        for (int i = 0; i < studentList.size(); i++) {
            if (studentList.get(i).check == true) {
                checkStudentNum++;
            }
        }

        if (checkStudentNum == 0) {
            pointList.get(index).check = true;
            Intent intent = new Intent(DriveActivity.this, PopupActivity.class);
            intent.putExtra("guide", "경유지 체크 완료");
            startActivity(intent);

        }
        for (int i = 0; i < studentList.size(); i++) {
            if (studentList.get(i).check == true) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("routeNo", course.id);
                contentValues.put("stdntNo", studentList.get(i).id);
                NetworkTask requestCheckPointTask = new NetworkTask(checkpointURL, contentValues, index, CHECK_POINT_TASK);
                requestCheckPointTask.execute();
            }
        }
    }

    public class NetworkTask extends AsyncTask {
        private String url;
        private ContentValues values;
        int point_index;
        int task;

        public NetworkTask(String url, ContentValues values, int index, int taskCode) {
            this.url = url;
            this.values = values;
            this.point_index = index;
            this.task = taskCode;
        }

        public NetworkTask(String url, ContentValues values, int taskCode) {
            this.url = url;
            this.values = values;
            this.task = taskCode;
        }

        @Override
        protected void onPreExecute() {
            if (task != SEND_LOCATION_TASK) {
                progressBar = findViewById(R.id.loading);
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String result;
            HttpClient httpClient = new HttpClient();
            result = httpClient.request(url, values);
            if (result == "" || result == null) {
                result = "driver drive activity networking result is empty";
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            Intent intent;
            super.onPostExecute(o);
            if (task != SEND_LOCATION_TASK)
                progressBar.setVisibility(View.GONE);
            switch (task) {
                case END_DIRVE_TASK:
                    poolGPSLocationTimer.cancel();
                    sendMyLocationTimer.cancel();
                    intent = new Intent(DriveActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case CHECK_POINT_TASK:
                    checkStudentNum--;
                    if (checkStudentNum == 0) {
                        pointList.get(point_index).check = true;
                        intent = new Intent(DriveActivity.this, PopupActivity.class);
                        intent.putExtra("guide", "경유지 체크 완료");
                        startActivity(intent);
                    }
                    break;
                case SEND_LOCATION_TASK:
                    break;
                case POOL_STUDENT_LOCATION_TASK:
                    parseStudentInfo(o.toString());
                    break;
            }
            //Todo after httpNetworking.
            //ex)Intent, terminate progress, list setting
        }
    }

    public void chanceFragment(int index) {
        curPointIdx = index;
        studentList = pointList.get(index).studentList;
        Log.d("drive_test", "selected_point = " + pointNameList.get(index));
        studentNameList = new ArrayList<>();
        studentListFragment = new StudentListFragment(studentList);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, studentListFragment).commit();
    }

    public void pointCheckError() {
        Intent intent = new Intent(DriveActivity.this, PopupActivity.class);
        intent.putExtra("guide", "이전 경유지가 완료되지 않았습니다.");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case END_DRIVE_CODE:
                if (resultCode == RESULT_OK) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("routeNo", course.id);
                    NetworkTask requestEndDrive = new NetworkTask(endDriveURL, contentValues, END_DIRVE_TASK);
                    requestEndDrive.execute();
                }
                break;
            case STUDENT_INFO_CODE:
                if (resultCode != RESULT_CANCELED) {
                    drawerButton.callOnClick();
                    requestStudentLocation();
                }
                break;
        }
    }

    public void endDrive() {
        Intent intent = new Intent(DriveActivity.this, ChoicePopupActivity.class);
        intent.putExtra("guide", "운행을 종료하시겠습니까?");
        startActivityForResult(intent, END_DRIVE_CODE);
    }

    public void endDriveError() {
        Intent intent = new Intent(DriveActivity.this, PopupActivity.class);
        intent.putExtra("guide", "모든 경유지를 체크하여야 운행 종료를 할 수 있습니다");
        startActivity(intent);
    }

    public void changeStudentFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, pointListFragment).commit();
    }

    public void initPointList() {
        pointList = course.pointList;
        pointNameList = new ArrayList<>();
        for (int i = 0; i < pointList.size(); i++) {
            pointNameList.add(pointList.get(i).name);
        }
    }

    public void initTmapPoint() {
        TMapPoint soongsilStation;
        TMapPoint jinLeeHall;
        TMapPoint jomansikHall;
        wayPoints = new ArrayList<>();
        try {
            startPoint = tMapUtil.findTitlePOI("숭실대학교 정보과학관");
            soongsilStation = tMapUtil.findTitlePOI("숭실대입구역");
            jinLeeHall = tMapUtil.findTitlePOI("서울대입구역");
            jomansikHall = tMapUtil.findTitlePOI("서울대학교");

            wayPoints.add(startPoint);
            wayPoints.add(soongsilStation);//종각역
            wayPoints.add(jinLeeHall);
            wayPoints.add(jomansikHall);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseStudentInfo(String result) {
        JSONObject resultJson;
        Log.d("drive_test", "student location = " + result);
        Double latitude = null;
        Double longitude = null;
        try {
            resultJson = new JSONObject(result);
            if (resultJson.getString("status").equals("REJECT") == true) {
                Intent intent = new Intent(this, PopupActivity.class);
                intent.putExtra("guide", "학생위치정보가 없습니다");
                startActivity(intent);
            } else {
                JSONObject location = resultJson.getJSONObject("location");
                latitude = location.getDouble("latitude");
                longitude = location.getDouble("longitude");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            latitude = 37.494870;
            longitude = 126.960763;
        }

        if (studentMarkerOption == null) {
            studentMarkerOption = new MarkerOptions();
            studentMarkerOption.position(new LatLng(latitude, longitude));

            studentMarker = mMap.addMarker(studentMarkerOption);
            studentMarker.setTitle(studentList.get(curStudentIdx).name);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        } else {
            studentMarker.setPosition(new LatLng(latitude, longitude));
            studentMarker.setTitle(studentList.get(curStudentIdx).name);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
    }

    public void convertTmapToGoogle() {
        pathPointList = new ArrayList<>();

        for (int i = 0; i < tMapUtil.multiPathPolyLine.getLinePoint().size(); i++) {
            pathPointList.add(new LatLng(tMapUtil.multiPathPolyLine.getLinePoint().get(i).getLatitude(),
                    tMapUtil.multiPathPolyLine.getLinePoint().get(i).getLongitude()));
        }
    }

    public void addPolygonPath() {
        pathPointList = new ArrayList<>();
        for (int i = 0; i < course.pathlist.size(); i++) {
            pathPointList.add(new LatLng(course.pathlist.get(i).latitude, course.pathlist.get(i).longitude));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        initTmapPoint();//initialize waypoints
//        tMapUtil.addMultiPointPath(wayPoints);//request find path from waypoints.
//        convertTmapToGoogle();//TMapPoint를 GoogleMap의 LatLng로 변경.
        addPolygonPath();
//        LatLng seoul = new LatLng(37.56, 126.97);
        LatLng seoul = pathPointList.get(0);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(seoul);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        Polyline polyline = mMap.addPolyline((new PolylineOptions()).clickable(true).addAll(pathPointList));
        polyline.setTag("firstpath");
    }
}
