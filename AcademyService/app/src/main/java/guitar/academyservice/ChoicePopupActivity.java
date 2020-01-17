package guitar.academyservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import guitar.academyservice.ui.login.LoginActivity;

public class ChoicePopupActivity extends Activity {
    TextView guideText;
    Button closeButton;
    Button okButton;

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
        return;
    }
}
