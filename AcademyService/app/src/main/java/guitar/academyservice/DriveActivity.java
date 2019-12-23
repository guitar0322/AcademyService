package guitar.academyservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

public class DriveActivity extends AppCompatActivity implements OnMapReadyCallback {
    Course course;
    ArrayList<Point> pointList;
    ArrayList<Student> studentList;
    ArrayList<TMapPoint> wayPoints;
    ArrayList<String> pointNameList;
    ArrayList<String> studentNameList;


    TMapPoint startPoint;
    TMapUtil tMapUtil;
    GPSManager gpsManager;
    Bitmap busicon;
    PointListFragment pointListFragment;
    StudentListFragment studentListFragment;
    private static final int POOL_GPS_TIME = 3000;
    private GoogleMap mMap;
    private ArrayList<LatLng> pathPointList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        tMapUtil = new TMapUtil(this);
        busicon = BitmapFactory.decodeResource(this.getResources(), R.drawable.busicon);

        gpsManager = new GPSManager(this);
        TimerTask poolGPSLocationTask = new TimerTask(){
            @Override
            public void run(){
                DriveActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gpsManager.getLocation();
                        tMapUtil.addCustomIconMark("busmark", busicon, 0.5f, 0,
                                new TMapPoint(gpsManager.getLatitude(), gpsManager.getLongitude()));
//                        Toast.makeText(DriveActivity.this, gpsManager.getLatitude() + "," + gpsManager.getLongitude(), Toast.LENGTH_SHORT).show();순서에 따라
                    }
                });
            }
        };
        Timer timer = new Timer();
        Intent intent = getIntent();
        course = (Course)intent.getSerializableExtra("course");
        initStudentList();
        pointListFragment = new PointListFragment(pointNameList);

        timer.schedule(poolGPSLocationTask, 0, POOL_GPS_TIME); //get gps location and make bus icon every POOL_GPS_TIME seconds.

        getSupportFragmentManager().beginTransaction().replace(R.id.container, pointListFragment).commit();

        tMapUtil.tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            public boolean onPressEvent(ArrayList arrayList, ArrayList arrayList1, TMapPoint tMapPoint, PointF pointF) {
                //Toast.makeText(MapEvent.this, "onPress~!", Toast.LENGTH_SHORT).show();
                return false;
            }

        @Override
            public boolean onPressUpEvent(ArrayList arrayList, ArrayList arrayList1, TMapPoint tMapPoint, PointF pointF) {
             //Toast.makeText(MapEvent.this, "onPressUp~!", Toast.LENGTH_SHORT).show();
              return false;
            }
        });
    }

    public void studentPopup(int index){
        Intent intent = new Intent(this, StudentInfoActivity.class);
        intent.putExtra("student", studentList.get(index));
        startActivity(intent);
    }

    public void convertTmapToGoogle(){
        pathPointList = new ArrayList<>();

        for(int i = 0; i < tMapUtil.multiPathPolyLine.getLinePoint().size(); i++){
            pathPointList.add(new LatLng(tMapUtil.multiPathPolyLine.getLinePoint().get(i).getLatitude(),
                    tMapUtil.multiPathPolyLine.getLinePoint().get(i).getLongitude()));
        }
    }
    public void chanceFragment(int index){
        studentList = pointList.get(index).studentList;
        Log.d("drive_test", "selected_point = " + pointNameList.get(index));
        studentNameList = new ArrayList<>();
        for(int i = 0; i < studentList.size(); i++){
            studentNameList.add(studentList.get(i).name);
        }
        Log.d("drive_test", "studentlist size = " + studentList.size());
        studentListFragment = new StudentListFragment(studentNameList, index);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, studentListFragment).commit();
    }

    public void pointCheckError(){
        Intent intent = new Intent(DriveActivity.this, PopupActivity.class);
        intent.putExtra("guide", "이전 경유지가 완료되지 않았습니다.");
        intent.putExtra("code", PopupActivity.BASIC);
        startActivity(intent);
    }

    public void endDrive(){
        Intent intent = new Intent(DriveActivity.this, PopupActivity.class);
        intent.putExtra("code", PopupActivity.END_DRIVE);
        intent.putExtra("guide", "운행을 종료하시겠습니까?");
        startActivity(intent);
    }

    public void endDriveError(){
        Intent intent = new Intent(DriveActivity.this, PopupActivity.class);
        intent.putExtra("code", PopupActivity.BASIC);
        intent.putExtra("guide", "모든 경유지를 체크하여야 운행 종료를 할 수 있습니다");
        startActivity(intent);
    }

    public void changeStudentFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.container, pointListFragment).commit();
    }

    public void initStudentList(){
        pointList = course.pointList;
        pointNameList = new ArrayList<>();
        for(int i = 0; i < pointList.size(); i++){
            pointNameList.add(pointList.get(i).name);
        }
    }
    public void initTmapPoint(){
        TMapPoint soongsilStation;
        TMapPoint jinLeeHall;
        TMapPoint  jomansikHall;
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
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initTmapPoint();//initialize waypoints
        tMapUtil.addMultiPointPath(wayPoints);//request find path from waypoints.
        convertTmapToGoogle();//TMapPoint를 GoogleMap의 LatLng로 변경.

//        LatLng seoul = new LatLng(37.56, 126.97);
        LatLng seoul = pathPointList.get(50);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(seoul);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        Polyline polyline = mMap.addPolyline((new PolylineOptions()).clickable(true).addAll(pathPointList));
        polyline.setTag("firstpath");
    }
}
