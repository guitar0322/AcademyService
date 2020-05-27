package com.gongamhada.driver;

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

public class PopupActivity extends Activity {
    TextView guideTextView;
    Button closeButton;
    int POPUP_CODE;
    String guideText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int y = metrics.heightPixels;
        int x = metrics.widthPixels;

        getWindow().setBackgroundDrawable(new PaintDrawable(Color.TRANSPARENT));
        getWindow().setLayout((int)(x * 0.7f), (int)(y * 0.25f));

        getWindow().setBackgroundDrawable(new PaintDrawable(Color.TRANSPARENT));

        guideTextView = findViewById(R.id.guideText);
        guideTextView.setPadding((int)(x*0.1f), (int)(y*0.028f), (int)(x*0.1f), 0);

        closeButton = findViewById(R.id.closeButton);

        Intent intent = getIntent();
        guideText = intent.getStringExtra("guide");
        guideTextView.setText(guideText);
        closeButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
