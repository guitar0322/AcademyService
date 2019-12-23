package guitar.student;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class DriveActivity extends AppCompatActivity implements OnMapReadyCallback {
    Academy academy;
    ArrayList<String> pointNameList;
    ArrayList<String> studentNameList;

    TextView academyNameView;
    TextView driverNameView;
    TextView driverPhoneView;
    TextView arriveTimeView;

    Bitmap busicon;
    private static final int POOL_GPS_TIME = 3000;
    private GoogleMap mMap;
    private ArrayList<LatLng> pathPointList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        academy = (Academy) intent.getSerializableExtra("academy");
        initAcademyInfo();
    }

    public void initAcademyInfo(){ ;
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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

//        Polyline polyline = mMap.addPolyline((new PolylineOptions()).clickable(true).addAll(pathPointList));
//        polyline.setTag("firstpath");
    }
}
