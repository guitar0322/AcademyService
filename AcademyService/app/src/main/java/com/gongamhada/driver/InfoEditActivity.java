package com.gongamhada.driver;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoEditActivity extends AppCompatActivity {
    private static final int EDIT_SUCCESS_CODE = 1;
    String username;
    String infoEditUrl;
    ProgressBar progressBar;
    EditText passwordEdit1;
    EditText passwordEdit2;
    Button editButton;
    Button backButton;
    String password1;
    String password2;
    TextView password1err;
    TextView password2err;
    TextView guideText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_edit);

        infoEditUrl = getString(R.string.url) + "driver/edit?";
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        passwordEdit1 = findViewById(R.id.password);
        passwordEdit2 = findViewById(R.id.password2);
        password1err = findViewById(R.id.password1_err);
        password2err = findViewById(R.id.password2_err);
        editButton = findViewById(R.id.edit);

        guideText = findViewById(R.id.guide);
        guideText.setText(UserInfo.instance.name+"님의 소중한 개인정보를 위해\n비밀번호를 재설정합니다");

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(InfoEditActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        editButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                password1 = passwordEdit1.getText().toString();
                password2 = passwordEdit2.getText().toString();
                if (checkPassword(password1) == true) {
                    password1err.setVisibility(View.INVISIBLE);
                    if(password1.equals(password2)) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("username", username);
                        contentValues.put("password", password1);
                        RequestInfoEdit requestInfoEdit = new RequestInfoEdit(infoEditUrl, contentValues);
                        requestInfoEdit.execute();
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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case EDIT_SUCCESS_CODE:
                SharedPreferences preferences = getSharedPreferences("AutoLogin", MODE_PRIVATE);;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", "");
                editor.putString("password","");
                editor.commit();
                Intent intent = new Intent(InfoEditActivity.this, LoginActivity.class);
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

    public class RequestInfoEdit extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestInfoEdit(String url, ContentValues values) {
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
                result = "infoedit activity networking test result";
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d("infoedit_test", "success");
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(InfoEditActivity.this, PopupActivity.class);
            intent.putExtra("guide", "비밀번호 변경이 완료되었습니다\n다시 로그인 해주시기 바랍니다");
            startActivityForResult(intent, EDIT_SUCCESS_CODE);

            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }

    }
}
