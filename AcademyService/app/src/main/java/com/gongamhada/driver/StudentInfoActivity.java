package com.gongamhada.driver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class StudentInfoActivity extends Activity {

    Student student;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_student_info);

        getWindow().setBackgroundDrawable(new PaintDrawable(Color.TRANSPARENT));

        Intent intent = getIntent();
        student = (Student)intent.getSerializableExtra("student");

        TextView nameView = findViewById(R.id.studentName);
        Button phoneButton = findViewById(R.id.studentPhone);
        Button parentPhoneButton = findViewById(R.id.parentPhone);
        Button locationButton = findViewById(R.id.studentLocation);
        Button closeButton = findViewById(R.id.stuInfoCloseButton);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        locationButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                setResult(RESULT_OK);
                finish();
            }
        });

        nameView.setText(student.name);

        Log.d("parent_test", student.parentPhone);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+student.phone)));
            }
        });

        parentPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+student.parentPhone)));
            }
        });
    }
}
