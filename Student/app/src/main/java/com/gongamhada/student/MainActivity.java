package com.gongamhada.student;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static final int APP_QUIT_CODE = 1;
    static final int LOGOUT_CODE = 2;

    String driveURL;
    ArrayList<Academy> academyList;
    Academy selectedAcademy;

    String username;
    TextView nameView;

    ListView academyListView;
    Toolbar toolbar;
    ActionBar actionBar;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        driveURL = getString(R.string.url) + "stdnt/driveDetail?";
        Intent intent = getIntent();
        username = UserInfo.instance.phone;
        nameView = findViewById(R.id.name);
        nameView.setText(UserInfo.instance.name+"님");
        if (academyList == null) {
            academyList = (ArrayList<Academy>) intent.getSerializableExtra("academy");
            Log.d("main_test", "academyList size is " + academyList.size());
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        academyListView = findViewById(R.id.academyList);
        AcademyListAdapter academyListAdapter = new AcademyListAdapter(this, academyList);
        academyListView.setAdapter(academyListAdapter);
        academyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent;
                selectedAcademy = academyList.get(position);
                if (checkLocationServicesStatus() == false) {
                    intent = new Intent(MainActivity.this, PopupActivity.class);
                    intent.putExtra("guide", "gps 기능을 활성화 하여야 합니다.");
                    startActivity(intent);
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("acaNo", selectedAcademy.id);
                    contentValues.put("courseNo", selectedAcademy.courseID);
                    contentValues.put("stdntNo", UserInfo.instance.subID);
                    RequestDrive requestDrive = new RequestDrive(driveURL, contentValues);
                    requestDrive.execute();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.account:
                intent = new Intent(MainActivity.this, InfoAuthActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.logout:
                intent = new Intent(MainActivity.this, ChoicePopupActivity.class);
                intent.putExtra("guide", "로그아웃 하시겠습니까?");
                startActivityForResult(intent, LOGOUT_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ChoicePopupActivity.class);
        intent.putExtra("guide", "앱을 종료하시겠습니까?");
        startActivityForResult(intent, APP_QUIT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case APP_QUIT_CODE:
                if (resultCode == RESULT_OK) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                break;
            case LOGOUT_CODE:
                if (resultCode == RESULT_OK) {
                    SharedPreferences preferences = getSharedPreferences("AutoLogin", MODE_PRIVATE);
                    ;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", "");
                    editor.putString("password", "");
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    public class RequestDrive extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestDrive(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            progressBar = findViewById(R.id.loading);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String result;
            HttpClient httpClient = new HttpClient();
            result = httpClient.request(url, values);
            if (result == "" || result == null) {
                result = "testresult";
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d("login_test", "login success");

            progressBar.setVisibility(View.GONE);
            parseDriveInfo(o.toString());
            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }
    }

    public void parseDriveInfo(String result) {
        JSONObject resultJson = null;
        try {
            resultJson = new JSONObject(result);
            Log.d("s_drive_test", result);
            if (resultJson.getString("status").equals("ACCEPT")) {
                selectedAcademy.arriveTime = resultJson.getString("arrive");
                Log.d("time_test", "" + selectedAcademy.arriveTime);
                Intent intent = new Intent(MainActivity.this, DriveActivity.class);
                intent.putExtra("academy", selectedAcademy);
                intent.putExtra("username", username);
                startActivity(intent);
            } else if(resultJson.getString("status").equals("NOTWORK")){
                Intent intent = new Intent(MainActivity.this, PopupActivity.class);
                intent.putExtra("guide", "운행중이 아닙니다.");
                startActivity(intent);
            }else if(resultJson.getString("status").equals("REJECT")){
                Intent intent = new Intent(MainActivity.this, PopupActivity.class);
                intent.putExtra("guide", "운행정보를 가져오는 도중 오류가 발생하였습니다.");
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(MainActivity.this, PopupActivity.class);
            intent.putExtra("guide", "네트워크 오류가 발생하였습니다. 인터넷을 확인 해주세요.");
            startActivity(intent);
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
