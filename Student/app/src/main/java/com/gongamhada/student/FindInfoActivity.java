package com.gongamhada.student;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class FindInfoActivity extends AppCompatActivity {
    String findInfoURL;
    String mPhoneNum;

    EditText phoneEditText;
    EditText emailEditText;
    TextView phoneErrText;
    TextView emailErrText;
    Button findButton;
    Button backButton;
    ProgressBar progressBar;

    @Override
    public void onBackPressed(){
        startActivity(new Intent(FindInfoActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_info);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            mPhoneNum = tm.getLine1Number();
        }
        catch(SecurityException e){
            Toast.makeText(this, "전화 퍼미션이 거부되었습니다. 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
            finish();
        }
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FindInfoActivity.this, LoginActivity.class));
                finish();
            }
        });
        findInfoURL = getString(R.string.url)+"/stdnt/find?";
        findButton = findViewById(R.id.find);
        phoneEditText = findViewById(R.id.phone);
        phoneErrText = findViewById(R.id.phone_err);
        emailEditText = findViewById(R.id.email);
        emailErrText = findViewById(R.id.email_err);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCond()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("tel", phoneEditText.getText().toString());
                    contentValues.put("email", emailEditText.getText().toString());

                    RequestFind requestSignup = new RequestFind(findInfoURL, contentValues);
                    requestSignup.execute();
                }
            }
        });
    }

    public boolean checkCond(){
        Intent intent;
        Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );
        if(phoneEditText.getText().toString().equals("") || emailEditText.getText().toString().equals("")){
            intent = new Intent(FindInfoActivity.this , PopupActivity.class);
            intent.putExtra("guide", "회원정보를 입력바랍니다.");
            startActivity(intent);
        }

        else {
            if (mPhoneNum.substring(3).equals(phoneEditText.getText().toString().substring(1)) == false) {
                phoneErrText.setVisibility(View.VISIBLE);
                return false;
            } else if (mPhoneNum.substring(3).equals(phoneEditText.getText().toString().substring(1))) {
                phoneErrText.setVisibility(View.INVISIBLE);
            }

            if (EMAIL_ADDRESS_PATTERN.matcher(emailEditText.getText().toString()).matches() == false) {
                emailErrText.setVisibility(View.VISIBLE);
                return false;
            } else {
                emailErrText.setVisibility(View.INVISIBLE);
            }
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 0:
                Intent intent = new Intent(FindInfoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    public class RequestFind extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestFind(String url, ContentValues values){
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
            Log.d("find_test", o.toString());
            progressBar.setVisibility(View.GONE);
            parseResult(o.toString());
            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }
    }

    public void parseResult(String result){
        if(result.equals("SUCCESS")){
            Intent intent = new Intent(FindInfoActivity.this , PopupActivity.class);
            intent.putExtra("guide", "이메일로 임시 비밀번호를 발급해드렸습니다 로그인 해주십시오");
            startActivityForResult(intent, 0);
        }
        else{
            Intent intent = new Intent(FindInfoActivity.this , PopupActivity.class);
            intent.putExtra("guide", "존재하지 않는 회원입니다");
            startActivity(intent);
        }
    }
}
