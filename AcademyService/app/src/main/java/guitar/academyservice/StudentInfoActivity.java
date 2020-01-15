package guitar.academyservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class StudentInfoActivity extends Activity {

    Student student;
    DriveActivity driveActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_student_info);

        Intent intent = getIntent();
        student = (Student)intent.getSerializableExtra("student");

        TextView nameView = findViewById(R.id.studentName);
        TextView phoneView = findViewById(R.id.studentPhone);
        TextView parentNameView = findViewById(R.id.parentName);
        TextView parentPhoneView = findViewById(R.id.parentPhone);

        nameView.setText(student.name);
        phoneView.setText(student.phone);
        parentNameView.setText(student.parentName);
        parentPhoneView.setText(student.parentPhone);
    }
}
