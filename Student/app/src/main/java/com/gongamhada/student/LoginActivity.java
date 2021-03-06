package com.gongamhada.student;


import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
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
import java.util.List;

import com.gongamhada.student.R;


public class LoginActivity extends AppCompatActivity {
    private static final int LOGIN_CODE = 1;
    private static final int APP_QUIT_CODE = 2;
    private static final int GPS_ENABLE_REQUEST_CODE = 2000;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE};

    String loginUrl;
    String username;
    String password;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ProgressBar progressBar;
    ArrayList<Academy> academyList;

    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    Button signupButton;
    Button eyeButton;
    Button findButton;
    CheckBox autoLoginCheckBox;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUrl = getString(R.string.url) + "stdnt/login?";

        if(checkAccessibilityPermissions() == false){
            setAccessibilityPermissions();
        }
        if(!checkLocationServicesStatus()){
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
            Log.d("login_test", "preference username = " + username);
            requestLogin();
        }

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);

        autoLoginCheckBox = findViewById(R.id.autoLogin);

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

        loginButton.setOnClickListener(new Button.OnClickListener(){
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


    public boolean checkAccessibilityPermissions(){
        AccessibilityManager accessibilityManager = (AccessibilityManager)getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);

        Log.d("service_test", "service_size : " + list.size());
        for(int i = 0; i < list.size(); i++){
            AccessibilityServiceInfo info = list.get(i);
            Log.d("accessibility_test", "packagename = " + info.getResolveInfo().serviceInfo.packageName);
            if(info.getResolveInfo().serviceInfo.packageName.equals(getApplication().getPackageName())){
                return true;
            }
        }
        return false;
    }

    public void setAccessibilityPermissions(){
        AlertDialog.Builder permissionDialog = new AlertDialog.Builder(this);
        permissionDialog.setTitle("접근성 권한 설정");
        permissionDialog.setMessage("유해앱 차단 기능 사용을 위해 앱 접근성 권한이 필요합니다.");
        permissionDialog.setPositiveButton("허용", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                return;
            }
        }).create().show();
    }

    public void checkGPSPermission(){
        int locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int phonePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if(locationPermissionCheck == PackageManager.PERMISSION_DENIED && coarsePermissionCheck == PackageManager.PERMISSION_DENIED
                && phonePermissionCheck == PackageManager.PERMISSION_DENIED){
            Log.d("permission_test", "denied");
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);        }
        else if(locationPermissionCheck == PackageManager.PERMISSION_GRANTED){
            Log.d("permission_test", "granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        boolean check_result = true;
        if ( requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
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

    public void requestLogin(){
        ContentValues loginData = new ContentValues();
        loginData.put("tel", username);
        loginData.put("password", password);
        loginData.put("token", getSharedPreferences("DeviceToken", MODE_PRIVATE).getString("token", ""));
        RequestLogin requestLogin = new RequestLogin(loginUrl, loginData);
        requestLogin.execute();
    }

    public void insertUserInfo(String username, String password){
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    public class RequestLogin extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestLogin(String url, ContentValues values){
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
            Log.d("login_test", "login success");
            parseAcademyInfo(o.toString());
            if(autoLoginCheckBox.isChecked() == true)
                insertUserInfo(username, password);

            progressBar.setVisibility(View.GONE);
            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }
    }

    public void parseAcademyInfo(String result){
        Log.d("login_test", "login result = " + result);
        try{
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("status").equals("NOTHING_MATCH_ACCNT")){
                Log.d("login_test", "REJECT");
                Intent intent = new Intent(LoginActivity.this, PopupActivity.class);
                intent.putExtra("guide", "로그인 정보가 일치하지 않습니다");
                startActivityForResult(intent, LOGIN_CODE);
                return;
            }
            else if(jsonObject.getString("status").equals("REJECT")){
                Intent intent = new Intent(LoginActivity.this, PopupActivity.class);
                intent.putExtra("guide", "회원정보를 입력해주세요");
                startActivityForResult(intent, LOGIN_CODE);
                return;
            }
            else {
                JSONArray academyJsonArray = new JSONArray(jsonObject.getString("academy"));
                editInfo(jsonObject.getJSONObject("stdntInfo"));
                academyList = new ArrayList<>();

                for (int i = 0; i < academyJsonArray.length(); i++) {
                    JSONObject academyInfo = academyJsonArray.getJSONObject(i);
                    JSONArray courseJsonArray = academyInfo.getJSONArray("courses");

                    for(int j = 0; j < courseJsonArray.length(); j++){
                        JSONObject courseInfo = courseJsonArray.getJSONObject(j);
                        academyList.add(new Academy(academyInfo.getInt("acaNo"), academyInfo.getString("acaName"), academyInfo.getString("acaTel"),
                                courseInfo.getInt("routeNo"), courseInfo.getString("routeName"),
                                courseInfo.getString("driver"), courseInfo.getString("phone"), courseInfo.getString("status"), courseInfo.getInt("busNo")));
                        UserInfo.instance.subID = courseInfo.getInt("stdntNo");
                    }

                }
//                academyList.add(new Academy(3, "없는학원", "01012412123",
//                        5, "없는운행",
//                        "없는기사", "01012312532", "N", 5));

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("academy", academyList);
                startActivity(intent);
                finish();
            }
        }
        catch(JSONException e){
//            academyList = new ArrayList<>();
//            academyList.add(new Academy("수학학원", "01044444444", 1,"동작아파트 월화", "이기사", "01011111111", "Y", 1));
//            academyList.add(new Academy("영어학원", "01055555555", 2,"동작초등학교 수목" ,"김기사", "01022222222", "N", 1));
//            e.printStackTrace();

            Log.d("login_test", "NETWORK_ERROR");
            Intent intent = new Intent(LoginActivity.this, PopupActivity.class);
            intent.putExtra("guide", "로그인중 오류가 발생하였습니다\n인터넷 연결을 확인해주세요");
            startActivityForResult(intent, LOGIN_CODE);
            return;
        }
    }

    public void editInfo(JSONObject info){
        try {
            UserInfo.instance.name = info.getString("name");
            UserInfo.instance.id = info.getInt("stdntNo");
            UserInfo.instance.phone = info.getString("tel");
            UserInfo.instance.password = password;
            editor.putInt("id", info.getInt("stdntNo"));
            editor.commit();
        }
        catch(Exception e){
            Log.e("login_error", "throw exception while edit userinfo.");
            e.printStackTrace();
        }
    }
}
