package guitar.parent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONObject;

public class InfoAuthActivity extends AppCompatActivity {
    String password;
    String username;
    String authUrl;
    EditText passwordEdit;
    Button authButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_auth);

        authUrl = getString(R.string.url) + "parent/editRequest?";
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        passwordEdit = findViewById(R.id.password);
        authButton = findViewById(R.id.login);

        authButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                password = passwordEdit.getText().toString();
                ContentValues contentValues = new ContentValues();
                contentValues.put("username", username);
                contentValues.put("password", password);
                RequestInfoAuth requestInfoAuth = new RequestInfoAuth(authUrl, contentValues);
                requestInfoAuth.execute();
            }
        });
    }

    public class RequestInfoAuth extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestInfoAuth(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }
        @Override
        protected  void onPreExecute(){
            progressBar = findViewById(R.id.loading);
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            String result;
            HttpClient httpClient = new HttpClient();
            result = httpClient.request(url, values);
            if(result == "" || result == null){
                result = "infoauth activity networking test result";
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Intent intent;
            progressBar.setVisibility(View.GONE);
            if(parseAuthResult(o.toString()) == true) {
                Log.d("infoauth_test", "success");
                intent = new Intent(InfoAuthActivity.this, InfoEditActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
            else{
                intent = new Intent(InfoAuthActivity.this, PopupActivity.class);
                intent.putExtra("guide", "회원정보가 일치하지 않습니다");
                startActivity(intent);
            }
            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }

    }

    public boolean parseAuthResult(String result){
        String status = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            status = jsonObject.getString("status");
            if(status.equals("ACCEPT")){
                return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if(status == null){
            return true;
        }
        return false;
    }
}
