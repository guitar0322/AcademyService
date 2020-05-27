package com.gongamhada.driver;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private static final int LOGIN_CODE = 1;
    private static final int APP_QUIT_CODE = 2;
    private static final int GPS_ENABLE_REQUEST_CODE = 2000;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE};

    String loginUrl;
    String username;
    String password;
    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    Button signupButton;
    Button eyeButton;
    Button findButton;
    boolean eyeToggle;
    ProgressBar progressBar;
    CheckBox autoLoginCheckBox;

    ArrayList<Course> courseList;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginUrl = getString(R.string.url) + "driver/login?";

        if(checkLocationServicesStatus() == false){
            showDialogForLocationServiceSetting();
        }
        else{
            checkGPSPermission();
        }
        preferences = getSharedPreferences("AutoLogin", MODE_PRIVATE);
        editor = preferences.edit();
        username = preferences.getString("username", "");
        password = preferences.getString("password", "");

        if(username != ""){
            Log.d("autologin_test", "preference username = " + username);
            requestLogin();
        }
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

        loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                Log.d("login_test", "input username = " + username);
                if(username == "" || username == null)
                    Log.d("login_test", "input username plz...");
                else {
                    requestLogin();
                }
            }
        });

        signupButton = findViewById(R.id.signup);
        signupButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        eyeButton =findViewById(R.id.eye);
        eyeButton.setOnClickListener(new Button.OnClickListener(){
            boolean eyeToggle;
            @Override
            public void onClick(View view){
                if(eyeToggle == false) {
                    passwordEditText.setInputType(0x00000091);
                    view.setBackground(getResources().getDrawable(R.drawable.eyex_icon, null));
                }
                else {
                    passwordEditText.setInputType(0x00000081);
                    view.setBackground(getResources().getDrawable(R.drawable.eye_icon, null));
                }
                eyeToggle = !eyeToggle;
            }
        });
        autoLoginCheckBox = findViewById(R.id.autoLogin);

        findButton = findViewById(R.id.findButton);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, ChoicePopupActivity.class);
        intent.putExtra("guide", "앱을 종료하시겠습니까?");
        startActivityForResult(intent, APP_QUIT_CODE);
    }

    public void requestLogin(){
        ContentValues loginData = new ContentValues();
        loginData.put("username", username);
        loginData.put("password", password);
        loginData.put("token", getSharedPreferences("DeviceToken", MODE_PRIVATE).getString("token", ""));
        RequestLoginTask requestLoginTask = new RequestLoginTask(loginUrl, loginData);
        requestLoginTask.execute();
    }

    public void insertUserInfo(String username, String password){
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    public class RequestLoginTask extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestLoginTask(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }
        @Override
        protected void onPreExecute(){
            progressBar = findViewById(R.id.loading);
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            String result;
            HttpClient httpClient = new HttpClient();
            result = httpClient.request(url, values);
            if(result == "" || result == null){
                result = "testresult";
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(autoLoginCheckBox.isChecked() == true)
                insertUserInfo(username, password);
            progressBar.setVisibility(View.GONE);
            parseResult(o.toString());

            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
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
            case LOGIN_CODE:
                usernameEditText.setText("");
                passwordEditText.setText("");
                break;
            case APP_QUIT_CODE:
                if(resultCode == RESULT_OK){
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
        }
    }

    public void checkGPSPermission(){
        int locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int phonePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if(locationPermissionCheck == PackageManager.PERMISSION_DENIED && coarsePermissionCheck == PackageManager.PERMISSION_DENIED
            && phonePermissionCheck == PackageManager.PERMISSION_DENIED){
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
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[2])) {

                    Toast.makeText(LoginActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                    finish();
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

    public void parseResult(String result){
        Log.d("login_test", "login result = " + result);
        try{
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("status").equals("REJECT")){
                Log.d("login_test", "REJECT");
                Intent intent = new Intent(LoginActivity.this, PopupActivity.class);
                intent.putExtra("guide", "로그인 정보가 일치하지 않습니다");
                startActivityForResult(intent, LOGIN_CODE);
                return;

            }
            else {
                JSONObject driverInfo = jsonObject.getJSONObject("drvInfo");
                editInfo(driverInfo);
                JSONArray jsonArray = new JSONArray(jsonObject.getString("course"));
                ArrayList<Course> courseList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject courseInfo = jsonArray.getJSONObject(i);
                    courseList.add(new Course(courseInfo.getInt("routeNo"), courseInfo.getString("name"),
                            courseInfo.getInt("acaNo"), courseInfo.getString("academy"),
                            courseInfo.getString("number"), courseInfo.getString("address"), null, courseInfo.getInt("busNo")));
                }

                this.courseList = courseList;
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("course", courseList);
                startActivity(intent);
                finish();
            }
        }
        catch(JSONException e){
            e.printStackTrace();
//            courseList = new ArrayList<>();
//            courseList.add(new Course(1, "숭실대학교 근처 월화", 1, "상도수학학원",
//                    "01011111111", "서울특별시 동작구 상도동 111-11",  new ArrayList<Point>(),0));
//            courseList.add(new Course(1, "상도동일대", 2, "상도태권도",
//                    "01022222222", "서울특별시 동작구 상도동 112-32",  new ArrayList<Point>(),0));
//            courseList.add(new Course(1, "동작아파트단지", 3, "동작수학학원",
//                    "01011111111", "서울특별시 동작구 상도동 111-11" ,  new ArrayList<Point>(),0));

            Log.d("login_test", "NETWORK_ERROR");
            Intent intent = new Intent(LoginActivity.this, PopupActivity.class);
            intent.putExtra("guide", "로그인 시도중 오류가 발생하였습니다. 인터넷 연결을 확인해주세요.");
            startActivityForResult(intent, LOGIN_CODE);
            return;
        }
    }

    public void editInfo(JSONObject info){
        try {
            UserInfo.instance.name = info.getString("drvName");
            UserInfo.instance.id = info.getInt("drvNo");
            UserInfo.instance.phone = info.getString("drvTel");
            UserInfo.instance.password = password;
        }
        catch(Exception e){
            Log.e("login_error", "throw exception while edit userinfo.");
            e.printStackTrace();
        }
    }
}
