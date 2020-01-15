package guitar.academyservice;

import androidx.appcompat.app.AppCompatActivity;
import guitar.academyservice.ui.login.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.prefs.Preferences;

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

        guideTextView = findViewById(R.id.guideText);
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
