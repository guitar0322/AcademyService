package guitar.student;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ChoicePopupActivity extends Activity {
    public static final int APP_QUIT_CODE = 1;
    TextView guideText;
    Button closeButton;
    Button okButton;
    int REQUESTCODE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choice_popup);

        guideText = findViewById(R.id.guideText);
        okButton = findViewById(R.id.okButton);
        closeButton = findViewById(R.id.closeButton);

        Intent intent = getIntent();
        String data = intent.getStringExtra("guide");
        REQUESTCODE = intent.getIntExtra("code", 0);
        guideText.setText(data);

        okButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                switch (REQUESTCODE){
                    case APP_QUIT_CODE:
                        android.os.Process.killProcess(android.os.Process.myPid());
                        break;
                }
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
        return;
    }
}
