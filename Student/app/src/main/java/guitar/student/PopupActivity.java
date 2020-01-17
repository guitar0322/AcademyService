package guitar.student;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class PopupActivity extends Activity {
    public static final int BASIC = 0;
    public static final int SIGNUP_POPUP = 1;
    public static final int END_DRIVE=2;
    public static final int INFO_EDIT=3;
    TextView guideTextView;
    Button closeButton;
    int POPUP_CODE;
    String guideText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        guideTextView = findViewById(R.id.guideText);
        closeButton = findViewById(R.id.closeButton);

        Intent intent = getIntent();
        guideText = intent.getStringExtra("guide");
        POPUP_CODE = intent.getIntExtra("code", BASIC);
        setButtonClickListener();
    }

    public void setButtonClickListener(){
        View.OnClickListener signupClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PopupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        };

        View.OnClickListener endDriveClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PopupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };

        View.OnClickListener basicClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };

        View.OnClickListener infoEditClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                SharedPreferences preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", "");
                editor.putString("password","");
                editor.commit();
                Intent intent = new Intent(PopupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        };

        switch(POPUP_CODE){
            case SIGNUP_POPUP:
                closeButton.setOnClickListener(signupClickListener);
                guideTextView.setText("회원가입 성공");
                break;
            case BASIC:
                closeButton.setOnClickListener(basicClickListener);
                guideTextView.setText(guideText);
                break;
            case END_DRIVE:
                closeButton.setOnClickListener(endDriveClickListener);
                guideTextView.setText(guideText);
                break;
            case INFO_EDIT:
                closeButton.setOnClickListener(infoEditClickListener);
                guideTextView.setText(guideText);
                break;
        }
    }

    @Override
    public void onBackPressed(){
        return;
    }
}
