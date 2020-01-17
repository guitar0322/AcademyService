package guitar.parent;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    String signupURL;
    String username;
    String password1;
    String password2;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signupURL = getString(R.string.url) + "parent/signup";

        Intent intent = getIntent();

        Button signupButton = findViewById(R.id.signup);
        EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText1 = findViewById(R.id.password);
        final EditText passwordEditText2 = findViewById(R.id.password2);

        username = intent.getStringExtra("username");
        usernameEditText.setText(username);

        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                password1 = passwordEditText1.getText().toString();
                password2 = passwordEditText2.getText().toString();
                if(checkPassword() == true){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("username", username);
                    contentValues.put("password", password1);
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
            Log.d("login_test", "login success");
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(SignupActivity.this, PopupActivity.class);
            startActivity(intent);
            finish();
            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }
    }
}
