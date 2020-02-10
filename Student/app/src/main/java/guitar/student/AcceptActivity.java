package guitar.student;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class AcceptActivity extends AppCompatActivity {
    String acceptURL;
    ProgressBar progressBar;
    ContentValues contentValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept);

        acceptURL = getString(R.string.url) + "stdnt/agree?";
        Intent intent = new Intent(this, ChoicePopupActivity.class);
        contentValues = new ContentValues();
        contentValues.put("stdntNo", getIntent().getIntExtra("studentID", 0));
        intent.putExtra("guide", getIntent().getStringExtra("name") + "님의 위치정보 열람에 동의하시겠습니까?");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 1:
                if(resultCode == RESULT_OK) {
                    AcceptAuth acceptAuth = new AcceptAuth(acceptURL, contentValues);
                    acceptAuth.execute();
                }
                else{
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                break;
        }
    }

    public class AcceptAuth extends AsyncTask {
        private String url;
        private ContentValues values;

        public AcceptAuth(String url, ContentValues values) {
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
                result = "main activity networking test result";
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressBar.setVisibility(View.GONE);
            android.os.Process.killProcess(android.os.Process.myPid());

            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }

    }
}
