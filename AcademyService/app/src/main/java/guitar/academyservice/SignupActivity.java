package guitar.academyservice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import guitar.academyservice.ui.login.LoginActivity;

public class SignupActivity extends AppCompatActivity {
    private static final int SIGNUP_CODE = 1;
    String signupURL;
    String username;
    String password1;
    String password2;
    String name;
    ProgressBar progressBar;

    EditText usernameEditText;
    EditText passwordEditText1;
    EditText passwordEditText2;
    EditText nameEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signupURL = getString(R.string.url) + "driver/signup?";


        Button signupButton = findViewById(R.id.signup);
        usernameEditText = findViewById(R.id.username);
        passwordEditText1 = findViewById(R.id.password);
        passwordEditText2 = findViewById(R.id.password2);
        nameEditText = findViewById(R.id.name);

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

    public boolean checkPassword(){
        if(password1.equals(password2)){
            return true;
        }
        else
            return false;
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
            Intent intent = new Intent(SignupActivity.this, PopupActivity.class);
            intent.putExtra("guide", "회원가입성공. 로그인 해주십시오.");
            startActivityForResult(intent, SIGNUP_CODE);
            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }
    }
}
