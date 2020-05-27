package com.gongamhada.parent;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static final int APP_QUIT_CODE = 1;
    static final int LOGOUT_CODE = 2;
    static final int BLOCK_CODE = 3;

    static final int BLOCK_TASK_CODE = 100;
    static final int DRIVE_TASK_CODE = 200;

    String blockURL;
    String driveURL;
    ArrayList<Student> studentList;
    Student selectedStudent;
    ImageButton selectedButton;

    String username;
    TextView nameView;
    Toolbar toolbar;
    ActionBar actionBar;
    ProgressBar progressBar;

    ExpandableListView studentListView;
    StudentListAdapter studentListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blockURL = getString(R.string.url) + "parent/block?";
        driveURL = getString(R.string.url) + "parent/drive?";
        Intent intent = getIntent();
        username = getSharedPreferences("UserInfo", MODE_PRIVATE).getString("username", "");

        nameView = findViewById(R.id.name);
        nameView.setText(UserInfo.instance.name+"님");

        if (studentList == null) {
            Log.d("main_test", "studentList is null");
            studentList = (ArrayList<Student>) intent.getSerializableExtra("student");
        }

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        studentListView = findViewById(R.id.studentList);
        studentListAdapter = new StudentListAdapter(this, this, R.layout.studentlist_layout, R.layout.drivelist_layout, studentList);
        studentListView.setAdapter(studentListAdapter);

    }

    public void expandStudent(int groupPosition, boolean isExpanded){
        if(!isExpanded){
            studentListView.expandGroup(groupPosition);
        }
        else{
            studentListView.collapseGroup(groupPosition);
        }
    }

    public void requestBlock(int position, ImageButton selectedButton){
        selectedStudent = studentList.get(position);
        this.selectedButton = selectedButton;
        Intent intent = new Intent(this, ChoicePopupActivity.class);
        intent.putExtra("guide", "유해앱 차단기능을 실행하시겠습니까?");
        startActivityForResult(intent, BLOCK_CODE);
    }

    public void openDriveInfo(int position){
        selectedStudent = studentList.get(position);
        Intent intent;
        if(selectedStudent.courseName.equals("")){
            intent = new Intent(MainActivity.this, PopupActivity.class);
            intent.putExtra("guide", "운행정보 없음 학원에 문의 바랍니다.");
            startActivity(intent);
        }
        else{
            ContentValues contentValues = new ContentValues();
            contentValues.put("aNumber", selectedStudent.academyID);
            if(selectedStudent.courseID != 0)
                contentValues.put("rNumber", selectedStudent.courseID);
            contentValues.put("sNumber", selectedStudent.id);
            NetworkTask requestDriveInfo = new NetworkTask(driveURL, contentValues, DRIVE_TASK_CODE);
            requestDriveInfo.execute();
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
                    SharedPreferences preferences = getSharedPreferences("AutoLogin", MODE_PRIVATE);;
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
                    contentValues.put("stdntNo", selectedStudent.tracking_id);
                    if(selectedStudent.block == true)
                        contentValues.put("status", "N");
                    else
                        contentValues.put("status", "Y");
                    NetworkTask requestBlockTask = new NetworkTask(blockURL, contentValues, BLOCK_TASK_CODE);
                    requestBlockTask.execute();
                }
        }
    }

    public class NetworkTask extends AsyncTask {
        private String url;
        private ContentValues values;
        private int task_code;

        public NetworkTask(String url, ContentValues values, int task_code){
            this.url = url;
            this.values = values;
            this.task_code = task_code;
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
            switch (task_code){
                case BLOCK_TASK_CODE:
                    selectedStudent.block = !selectedStudent.block;
                    if(selectedStudent.block){
                        selectedButton.setImageDrawable(getDrawable(R.drawable.block_on));
                    }
                    else
                        selectedButton.setImageDrawable(getDrawable(R.drawable.block_off));
                    break;
                case DRIVE_TASK_CODE:
                    parseDriveInfo(o.toString());
                    break;
            }
            progressBar.setVisibility(View.GONE);

            //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }
    }

    public void parseDriveInfo(String result){
        JSONObject jsonResult;
        try{
            jsonResult = new JSONObject(result);
            if(jsonResult.getString("status").equals("ACCEPT")){
                selectedStudent.arriveTime = jsonResult.getString("arrive");
                Intent intent = new Intent(this, DriveActivity.class);
                intent.putExtra("student", selectedStudent);
                startActivity(intent);
            }
            else if(jsonResult.getString("status").equals("IN_COMPLETE")){
                Intent intent = new Intent(this, PopupActivity.class);
                intent.putExtra("guide", "이미 등원완료된 운행입니다");
                startActivity(intent);
            }
            else if(jsonResult.getString("status").equals("OUT_COMPLETE")){
                Intent intent = new Intent(this, PopupActivity.class);
                intent.putExtra("guide", "이미 하원완료된 운행입니다");
                startActivity(intent);
            }
            else if(jsonResult.getString("status").equals("NOTWORK")){
                Intent intent = new Intent(this, PopupActivity.class);
                intent.putExtra("guide", "운행중이 아닙니다");
                startActivity(intent);
            }
            else if(jsonResult.getString("status").equals("REJECT")){
                Intent intent = new Intent(this, PopupActivity.class);
                intent.putExtra("guide", "자녀 위치정보 열람 동의가 필요합니다\n자녀의 핸드폰에서 동의를 완료해주세요");
                startActivity(intent);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            Intent intent = new Intent(this, PopupActivity.class);
            intent.putExtra("guide", "네트워크 오류가 발생하였습니다. 인터넷을 확인해주세요");
            startActivity(intent);
        }
    }
}
