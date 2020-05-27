package com.gongamhada.student;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.gongamhada.student.R;

public class ChoicePopupActivity extends Activity {
    public static final int APP_QUIT_CODE = 1;
    public static final int QUIT_DRIVE = 2;
    public static final int LOGOUT = 3;
    TextView guideText;
    Button closeButton;
    Button okButton;
    int REQUESTCODE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choice_popup);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int y = metrics.heightPixels;
        int x = metrics.widthPixels;

        getWindow().setBackgroundDrawable(new PaintDrawable(Color.TRANSPARENT));
        getWindow().setLayout((int)(x * 0.7f), (int)(y * 0.25f));

        getWindow().setBackgroundDrawable(new PaintDrawable(Color.TRANSPARENT));

        guideText = findViewById(R.id.guideText);
        guideText.setPadding((int)(x*0.1f), (int)(y*0.028f), (int)(x*0.1f), 0);
        okButton = findViewById(R.id.okButton);
        okButton.setPadding((int)(x*0.06f), 0 , 0 , (int)(y * 0.03f));
        closeButton = findViewById(R.id.closeButton);
        closeButton.setPadding(0, 0 , (int)(x*0.06f) , (int)(y * 0.03f));

        Intent intent = getIntent();
        String data = intent.getStringExtra("guide");
        guideText.setText(data);

        okButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                setResult(RESULT_OK);
                finish();
            }
        });

        closeButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }



    @Override
    public void onBackPressed(){
        finish();
        return;
    }
}
