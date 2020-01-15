package guitar.parent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    static final int APP_QUIT_CODE = 1;
    static final int LOGOUT_CODE = 2;

    ArrayList<Student> studentList;
    ArrayList<String> studentNameList;
    Student selectedStudent;

    String username;

    Button infoButton;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ListView studentListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        username = getSharedPreferences("UserInfo", MODE_PRIVATE).getString("username", "");
        if (studentList == null) {
            Log.d("main_test", "studentList is null");
            studentList = (ArrayList<Student>) intent.getSerializableExtra("student");
            initStudentList();
        }

        studentListView = findViewById(R.id.studentList);
        infoButton = findViewById(R.id.infoButton);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);
        infoButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        ListAdapter studentAdapter = new ListAdapter(this, studentNameList);

        studentListView.setAdapter(studentAdapter);
        studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
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
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if (id == R.id.account) {
                    Intent intent = new Intent(MainActivity.this, InfoAuthActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);

                } else if (id == R.id.setting) {
                    Log.d("navigation_test", "setting selected");
                } else if (id == R.id.logout) {
                    Intent intent = new Intent(MainActivity.this, ChoicePopupActivity.class);
                    intent.putExtra("guide", "로그아웃 하시겠습니까?");
                    startActivityForResult(intent, LOGOUT_CODE);
                }
                return false;
            }
        });
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
        }
    }

    public void initStudentList() {
        studentNameList = new ArrayList<>();
        for (int i = 0; i < studentList.size(); i++) {
            studentNameList.add(studentList.get(i).name);
        }
    }
}
