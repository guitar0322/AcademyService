package guitar.parent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;

import java.util.ArrayList;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    static final int APP_QUIT_CODE = 1;
    static final int LOGOUT_CODE = 2;
    static final int BLOCK_CODE = 3;

    String blockURL;
    ArrayList<Student> studentList;
    Student selectedStudent;
    Switch selectedSwitch;

    String username;
    Toolbar toolbar;
    ActionBar actionBar;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blockURL = getString(R.string.url) + "parent/block?";
        Intent intent = getIntent();
        username = getSharedPreferences("UserInfo", MODE_PRIVATE).getString("username", "");
        if (studentList == null) {
            Log.d("main_test", "studentList is null");
            studentList = (ArrayList<Student>) intent.getSerializableExtra("student");
        }

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        ListView studentListView = findViewById(R.id.studentList);
        StudentListAdapter studentAdapter = new StudentListAdapter(this, this, studentList);
//
        studentListView.setAdapter(studentAdapter);

    }

    public void requestBlock(int position, Switch selectedSwitch){
        selectedStudent = studentList.get(position);
        this.selectedSwitch = selectedSwitch;
        Intent intent = new Intent(this, ChoicePopupActivity.class);
        intent.putExtra("guide", "유해앱 차단기능을 실행하시겠습니까?");
        startActivityForResult(intent, BLOCK_CODE);
    }

    public void openDriveInfo(int position){
        selectedStudent = studentList.get(position);
        Intent intent;
        if (selectedStudent.status == true) {
            intent = new Intent(MainActivity.this, DriveActivity.class);
            intent.putExtra("student", selectedStudent);
            startActivity(intent);
        } else {
            intent = new Intent(MainActivity.this, PopupActivity.class);
            intent.putExtra("guide", "운행중이 아닙니다");
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch(item.getItemId()){
            case R.id.account:
                intent = new Intent(MainActivity.this, InfoAuthActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.logout:
                intent = new Intent(MainActivity.this, ChoicePopupActivity.class);
                intent.putExtra("guide", "로그아웃 하시겠습니까?");
                startActivityForResult(intent, LOGOUT_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ChoicePopupActivity.class);
        intent.putExtra("guide", "앱을 종료하시겠습니까?");
        startActivityForResult(intent, APP_QUIT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case APP_QUIT_CODE:
                if(resultCode == RESULT_OK){
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                break;
            case LOGOUT_CODE:
                if(resultCode == RESULT_OK){
                    SharedPreferences preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", "");
                    editor.putString("password","");
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case BLOCK_CODE:
                if(resultCode == RESULT_OK){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("stdntNo", selectedStudent.id);
                    if(selectedStudent.block == true)
                        contentValues.put("status", "N");
                    else
                        contentValues.put("status", "Y");
                    RequestBlockTask requestBlockTask = new RequestBlockTask(blockURL, contentValues);
                    requestBlockTask.execute();
                    selectedStudent.block = !selectedStudent.block;
                }
                else{
                    selectedSwitch.toggle();
                }
        }
    }

    public class RequestBlockTask extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestBlockTask(String url, ContentValues values){
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
            progressBar.setVisibility(View.GONE);

            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }
    }

}
