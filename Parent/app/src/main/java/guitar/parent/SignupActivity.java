package guitar.parent;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {
    private static final int SIGNUP_CODE = 1;
    private static final int SIGNUP_FAIL_CODE=2;
    String signupURL;
    String username;
    String password1;
    String password2;
    String name;

    EditText usernameEditText;
    EditText passwordEditText1;
    EditText passwordEditText2;
    EditText nameEditText;
    Button signupButton;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signupURL = getString(R.string.url) + "parent/signup?";

        Intent intent = getIntent();

        signupButton = findViewById(R.id.signup);
        usernameEditText = findViewById(R.id.username);
        passwordEditText1 = findViewById(R.id.password);
        passwordEditText2 = findViewById(R.id.password2);
        nameEditText = findViewById(R.id.name);

        username = intent.getStringExtra("username");
        usernameEditText.setText(username);

        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                username = usernameEditText.getText().toString();
                password1 = passwordEditText1.getText().toString();
                password2 = passwordEditText2.getText().toString();
                name = nameEditText.getText().toString();
                if(checkPassword() == true){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("username", username);
                    contentValues.put("password", password1);
                    contentValues.put("name", name);
                    SharedPreferences preferences = getSharedPreferences("DeviceToken", MODE_PRIVATE);;

                    contentValues.put("token", preferences.getString("token", ""));
                    RequestSignup requestSignup = new RequestSignup(signupURL, contentValues);
                    requestSignup.execute();
                }
                else{
                    passwordEditText1.setHighlightColor(getResources().getColor(R.color.colorAccent));
                    passwordEditText1.requestFocus();
                    passwordEditText1.setText("");
                    passwordEditText2.setText("");
                    passwordEditText1.setHint("비밀번호가 일치하지 않습니다.");
                    passwordEditText1.setHintTextColor(getResources().getColor(R.color.red));
                }
            }
        });
    }

    public boolean checkPassword(){
        if(password1.equals(password2)){
            return true;
        }
        else
            return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case SIGNUP_CODE:
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
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
            if(result.getString("status").equals("DOUBLE")){
                intent = new Intent(SignupActivity.this, PopupActivity.class);
                intent.putExtra("guide", "이미 사용중인 회원정보입니다");
                startActivityForResult(intent, SIGNUP_FAIL_CODE);
            }
            else if(result.getString("status").equals("ACCEPT")){
                intent = new Intent(SignupActivity.this, PopupActivity.class);
                intent.putExtra("guide", "회원가입 성공. 로그인 하여 주십시오");
                startActivityForResult(intent, SIGNUP_CODE);
                finish();
            }
            else if(result.getString("status").equals("NOT_REGED_BY_ACA")){
                intent = new Intent(SignupActivity.this, PopupActivity.class);
                intent.putExtra("guide", "등록정보가 없습니다. 학원에 문의바랍니다.");
                startActivityForResult(intent, SIGNUP_FAIL_CODE);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            intent = new Intent(SignupActivity.this, PopupActivity.class);
            intent.putExtra("guide", "회원가입 성공. 로그인 하여 주십시오");
            startActivityForResult(intent, SIGNUP_CODE);
        }
    }
}
