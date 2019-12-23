package guitar.academyservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.OnLifecycleEvent;
import guitar.academyservice.ui.login.LoginActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ArrayList<Academy> academyList;
    ArrayList<String> academyNameList;
    Academy selectedAcademy;
    private static final int GPS_ENABLE_REQUEST_CODE = 2000;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    ContentValues data;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    String username;
    String mainURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainURL = getString(R.string.url) + "main";
        if(!checkLocationServicesStatus()){
            showDialogForLocationServiceSetting();
        }
        else{
            checkGPSPermission();
        }
        ListView academyListView = findViewById(R.id.academyList);
        Intent intent = getIntent();
        username = getSharedPreferences("UserInfo", MODE_PRIVATE).getString("username", "");
        if(academyList == null){
            Log.d("main_test", "academyList is null");
            academyList = (ArrayList<Academy>)intent.getSerializableExtra("Academy");
            initAcademyList();
        }
        data = new ContentValues();


        ListAdapter academyAdapter = new ListAdapter(this, academyNameList);

        academyListView.setAdapter(academyAdapter);
        academyListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView parent, View v, int position, long id){
                selectedAcademy = academyList.get(position);
                data.put("username", username);
                data.put("academy", academyList.get(position).name);
                RequestCourseList requestCourseList = new RequestCourseList(mainURL, data);
                requestCourseList.execute();
            }
        });
    }

    public class RequestCourseList extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestCourseList(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            String result;
            HttpClient httpClient = new HttpClient();
            result = httpClient.request(url, values);
            if(result == "" || result == null){
                result = "main activity networking test result";
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            parseCourseList(o.toString());
            Log.d("main_test", "selected academy's courselist size = " + selectedAcademy.courseList.size());
            Intent intent = new Intent(MainActivity.this, AcademyActivity.class);
            intent.putExtra("academy", selectedAcademy);
            startActivity(intent);

            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, ChoicePopupActivity.class);
        intent.putExtra("guide", "앱을 종료하시겠습니까?");
        intent.putExtra("code", PopupManager.APP_QUIT_CODE);
        startActivity(intent);
    }

    public void checkGPSPermission(){
        int locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(locationPermissionCheck == PackageManager.PERMISSION_DENIED && coarsePermissionCheck == PackageManager.PERMISSION_DENIED){
            Log.d("permissionCheck", "denied");
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }
        else if(locationPermissionCheck == PackageManager.PERMISSION_GRANTED){
            Log.d("permissionCheck", "granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        if ( requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if(check_result == false) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                if (checkLocationServicesStatus()) {
                        Log.d("checkGPSEnable", "onActivityResult : GPS 활성화 되있음");
                        checkGPSPermission();
                        return;
                }
                else{
                    Log.d("checkGPSEnable", "GPS 비활성화 되있음");
                }
                break;
        }
    }
    public void parseCourseList(String result){
        Log.d("main_test", "selected academy name = " + selectedAcademy.name);
        selectedAcademy.courseList = new ArrayList<Course>();
        selectedAcademy.courseList.add(new Course("동작구 일대", new ArrayList<Point>()));
        selectedAcademy.courseList.add(new Course("상도동 일대", new ArrayList<Point>()));
        selectedAcademy.courseList.add(new Course("남성동 일대", new ArrayList<Point>()));
        selectedAcademy.courseList.add(new Course("이수동 일대", new ArrayList<Point>()));
        //Todo
        //Setting to courseList of academy Object using result of networking.
    }
    public void initAcademyList(){
        academyNameList = new ArrayList<>();
        for(int i = 0; i < academyList.size(); i++){
            academyNameList.add(academyList.get(i).name);
        }
    }
}
