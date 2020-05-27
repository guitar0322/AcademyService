package com.gongamhada.driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChoicePopupActivity extends Activity {
    TextView guideText;
    Button closeButton;
    Button okButton;

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
