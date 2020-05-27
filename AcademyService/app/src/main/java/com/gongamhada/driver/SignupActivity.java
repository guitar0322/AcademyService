package com.gongamhada.driver;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.system.Os;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private static final int SIGNUP_CODE = 1;
    private static final int SIGNUP_FAIL_CODE=2;
    private static final int PHONE_PERMISSION_CODE = 1000;
    String signupURL;
    String phone;
    String password1;
    String password2;
    String name;
    String mPhoneNum;
    ProgressBar progressBar;

    EditText nameEditText;
    EditText phoneEditText;
    EditText passwordEditText1;
    EditText passwordEditText2;
    TextView password1err;
    TextView password2err;

    CheckBox privacyAccept;
    Button privacyPolicy;
    Button signupButton;
    Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signupURL = getString(R.string.url) + "driver/signup?";
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            mPhoneNum = tm.getLine1Number();
        }
        catch(SecurityException e){
            Toast.makeText(this, "전화 퍼미션이 거부되었습니다. 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
            finish();
        }
        Log.d("phone_test", mPhoneNum.substring(3));
        signupButton = findViewById(R.id.signup);
        loginButton = findViewById(R.id.login);
        nameEditText = findViewById(R.id.name);
        phoneEditText = findViewById(R.id.phone);
        privacyAccept = findViewById(R.id.accept);

        privacyPolicy = findViewById(R.id.policy_detail);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, PrivacyAcceptActivity.class);
                startActivity(intent);
            }
        });

        passwordEditText1 = findViewById(R.id.password1);
        passwordEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(checkPassword(s.toString())){
                    password1err.setVisibility(View.INVISIBLE);
                }
                else{
                    password1err.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        passwordEditText2 = findViewById(R.id.password2);
        password1err = findViewById(R.id.password1_err);
        password2err = findViewById(R.id.password2_err);

        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                phone = phoneEditText.getText().toString();
                password1 = passwordEditText1.getText().toString();
                password2 = passwordEditText2.getText().toString();
                name = nameEditText.getText().toString();

                if(checkSignupCond() == false){
                    return;
                }

                if(checkPassword(password1) == true){
                    password1err.setVisibility(View.INVISIBLE);
                    if(password1.equals(password2)) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("username", phone);
                        contentValues.put("password", password1);
                        contentValues.put("name", name);
                        contentValues.put("token", getSharedPreferences("DeviceToken", MODE_PRIVATE).getString("token", ""));

                        RequestSignup requestSignup = new RequestSignup(signupURL, contentValues);
                        requestSignup.execute();
                    }
                    else{
                        password2err.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    password1err.setVisibility(View.VISIBLE);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public boolean checkSignupCond(){
        Intent intent;
        if(name.equals("") || phone.length() < 11){
            intent = new Intent(SignupActivity.this , PopupActivity.class);
            intent.putExtra("guide", "올바르지 않은 입력입니다.");
            startActivity(intent);
            return false;
        }

        else if(privacyAccept.isChecked() == false){
            intent = new Intent(SignupActivity.this , PopupActivity.class);
            intent.putExtra("guide", "개인정보 처리방침에 동의해주세요.");
            startActivity(intent);
            return false;
        }
        else if(mPhoneNum.substring(3).equals(phone.substring(1)) == false){
            intent = new Intent(SignupActivity.this , PopupActivity.class);
            intent.putExtra("guide", "연락처가 일치하지 않습니다.\n");
            startActivity(intent);
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case SIGNUP_CODE:
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    public boolean checkPassword(String target){
        String passwordReg = "^((?=.*[a-zA-Z])(?=.*[0-9])).{8,15}$";

        Pattern pattern = Pattern.compile(passwordReg);
        Matcher matcher = pattern.matcher(target);

        return matcher.matches();
    }

    public class RequestSignup extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestSignup(String url, ContentValues values){
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
            Log.d("signup_test", o.toString());
            progressBar.setVisibility(View.GONE);
            parseSignupResult(o.toString());
            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }
    }

    public void parseSignupResult(String info){
        Intent intent;
        try{
            JSONObject result = new JSONObject(info);
            if(result.getString("status").equals("ALREADY_USED")){
                intent = new Intent(SignupActivity.this, PopupActivity.class);
                intent.putExtra("guide", "이미 사용중인 회원정보입니다");
                startActivityForResult(intent, SIGNUP_FAIL_CODE);
            }
            else if(result.getString("status").equals("ACCEPT")){
                intent = new Intent(SignupActivity.this, PopupActivity.class);
                intent.putExtra("guide", "회원가입 성공. 로그인 하여 주십시오");
                startActivityForResult(intent, SIGNUP_CODE);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            intent = new Intent(SignupActivity.this, PopupActivity.class);
            intent.putExtra("guide", "회원가입중 오류가 발생하였습니다. 네트워크를 확인해주세요.");
        }
    }
}
