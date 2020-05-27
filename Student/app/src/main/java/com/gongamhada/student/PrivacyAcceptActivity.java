package com.gongamhada.student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PrivacyAcceptActivity extends AppCompatActivity {
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_accept);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(PrivacyAcceptActivity.this , SignupActivity.class);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PrivacyAcceptActivity.this , SignupActivity.class);
        finish();
    }

}
