package guitar.parent;


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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.prefs.Preferences;


public class LoginActivity extends AppCompatActivity {
    private static final int LOGIN_CODE = 1;
    private static final int APP_QUIT_CODE = 2;

    private static final int GPS_ENABLE_REQUEST_CODE = 2000;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    String loginUrl;
    String username;
    String password;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ArrayList<Student> studentList;

    CheckBox autoLoginCheckBox;
    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    Button signupButton;
    ProgressBar progressBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUrl = getString(R.string.url) + "parent/login?";

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
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
        signupButton = findViewById(R.id.signup);
        autoLoginCheckBox = findViewById(R.id.autoLogin);

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

        signupButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, ChoicePopupActivity.class);
        intent.putExtra("guide", "앱을 종료하시겠습니까?");
        startActivityForResult(intent, APP_QUIT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
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
            case APP_QUIT_CODE:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case LOGIN_CODE:
                usernameEditText.setText("");
                passwordEditText.setText("");
                break;
        }
    }

    public void checkGPSPermission() {
        int locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (locationPermissionCheck == PackageManager.PERMISSION_DENIED && coarsePermissionCheck == PackageManager.PERMISSION_DENIED) {
            Log.d("permissionCheck", "denied");
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        } else if (locationPermissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.d("permissionCheck", "granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if (check_result == false) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(LoginActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
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
        loginData.put("username", username);
        loginData.put("password", password);
        SharedPreferences preferences = getSharedPreferences("DeviceToken", MODE_PRIVATE);;

        loginData.put("token", preferences.getString("token", ""));

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
            Log.d("login_test", "login success");

            progressBar.setVisibility(View.GONE);
            if(autoLoginCheckBox.isChecked() == true)
                insertUserInfo(username, password);

            parseStudentInfo(o.toString());
            //Todo after httpNetworking.
            //ex)Intent, terminate progress, studentlist setting
        }
    }

    public void parseStudentInfo(String result){
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
                JSONArray studentJsonArray = jsonObject.getJSONArray("student");
                for (int i = 0; i < studentJsonArray.length(); i++) {
                    JSONObject studentInfo = studentJsonArray.getJSONObject(i);
                    studentList = new ArrayList<>();
                    studentList.add(new Student(studentInfo.getInt("sNumber"), studentInfo.getInt("stdntNo"),
                            studentInfo.getString("sName"), studentInfo.getString("sTel"), studentInfo.getString("sStatus"),
                            studentInfo.getString("aName"), studentInfo.getString("aTel"),
                            studentInfo.has("rNumber")?studentInfo.getInt("rNumber"):0,
                            studentInfo.has("rName")?studentInfo.getString("rName"):"",
                            studentInfo.has("dNumber")?studentInfo.getInt("dNumber"):0,
                            studentInfo.has("dName")?studentInfo.getString("dName"):"",
                            studentInfo.has("dTel")?studentInfo.getString("dTel"):"",
                            studentInfo.has("bStatus")?studentInfo.getString("bStatus"):"",
                            studentInfo.has("bNumber")?studentInfo.getInt("bNumber"):0));
                }
            }
        }
        catch(JSONException e){
            e.printStackTrace();
            studentList = new ArrayList<>();
            studentList.add(new Student(1,0,"김동현",
                    "01012345678",  "N",
                    "수학학원", "01044444444",
                    1,"동작구일대 월화",
                    1,"이기사", "01011111111","Y", 0));

            studentList.add(new Student(1,0,"김동현",
                    "01012345678",  "N",
                    "영어학원", "01055555555",
                    1, "동작아파트 수목",
                    2,"김기사", "01022222222","N",0));

            studentList.add(new Student(2,0,"김동찬",
                    "01023456789",   "N",
                    "수학학원", "01066666666",
                    1,"동작초등학교정문 월화",
                    3,"최기사", "01033333333", "N",0));
        }
        editInfo();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("student", studentList);
        startActivity(intent);
        finish();
    }

    public void editInfo(){
        SharedPreferences preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.commit();
    }
}
