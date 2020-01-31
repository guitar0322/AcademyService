package guitar.parent;

import androidx.appcompat.app.AppCompatActivity;

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


public class InfoEditActivity extends AppCompatActivity {
    private static final int EDIT_INFO_CODE = 1;
    String password1;
    String password2;
    String username;
    String infoEditUrl;
    ProgressBar progressBar;
    EditText passwordEdit1;
    EditText passwordEdit2;
    Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_edit);

        infoEditUrl = getString(R.string.url) + "parent/edit?";
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        passwordEdit1 = findViewById(R.id.password);
        passwordEdit2 = findViewById(R.id.password2);
        editButton = findViewById(R.id.edit);

        editButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                password1 = passwordEdit1.getText().toString();
                password2 = passwordEdit2.getText().toString();
                if (checkPassword() == true) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("username", username);
                    contentValues.put("password", password2);
                    RequestInfoEdit requestInfoEdit = new RequestInfoEdit(infoEditUrl, contentValues);
                    requestInfoEdit.execute();
                }
                else{
                    passwordEdit1.setHighlightColor(getResources().getColor(R.color.colorAccent));
                    passwordEdit1.requestFocus();
                    passwordEdit1.setText("");
                    passwordEdit2.setText("");
                    passwordEdit1.setHint("비밀번호가 일치하지 않습니다.");
                    passwordEdit1.setHintTextColor(getResources().getColor(R.color.red));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case EDIT_INFO_CODE:
                SharedPreferences preferences = getSharedPreferences("AutoLogin", MODE_PRIVATE);;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", "");
                editor.putString("password","");
                editor.commit();
                Intent intent = new Intent(InfoEditActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
                break;
        }
    }
    public boolean checkPassword() {
        if (password1.equals(password2))
            return true;
        else
            return false;
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
            intent.putExtra("guide", "회원정보 변경에 성공하였습니다.\n다시 로그인 해주십시오.");
            startActivityForResult(intent, EDIT_INFO_CODE);

            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }
    }


}
