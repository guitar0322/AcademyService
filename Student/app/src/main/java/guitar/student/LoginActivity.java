package guitar.student;


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

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {
    String loginUrl;
    String username;
    String password;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ProgressBar progressBar;
    ArrayList<Academy> academyList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUrl = getString(R.string.url) + "drivers/test?";
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        username = preferences.getString("username", "");
        if(username != ""){
            Log.d("login_test", "preference username = " + username);
        }
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                Log.d("login_test", "input username = " + username);
                if(username == "" || username == null)
                    Log.d("login_test", "input username plz...");
                else {
                    ContentValues loginData = new ContentValues();
                    loginData.put("username", username);
                    loginData.put("password", password);
                    RequestLogin requestLogin = new RequestLogin(loginUrl, loginData);
                    requestLogin.execute();
                }
            }
        });
    }
    public void insertUserInfo(String username){
        editor.putString("username", username);
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
            initStudentList(o.toString());
            insertUserInfo(username);
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("academy", academyList);
            startActivity(intent);
            finish();
            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }
    }

    public void initStudentList(String result){
        Log.d("login_test", "login result = " + result);
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = new JSONArray(jsonObject.getString("acaList"));
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject academyInfo = jsonArray.getJSONObject(i);
                Log.d("json_test", "academy name = " + academyInfo.getString("name"));
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        academyList = new ArrayList<>();
        academyList.add(new Student("김동현", "이기사", "01011111111", "수학학원", "01044444444"));
        academyList.add(new Student("김동현", "김기사", "01022222222", "영어학원", "01055555555"));
        academyList.add(new Student("김동찬", "최기사", "01033333333", "수학학원", "01066666666"));
    }
}
