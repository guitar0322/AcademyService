package guitar.academyservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignupActivity extends AppCompatActivity {
    String signupURL;
    String username;
    String password1;
    String password2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signupURL = getString(R.string.url) + "signup";

        Button signupButton = findViewById(R.id.signup);
        EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText1 = findViewById(R.id.password);
        EditText passwordEditText2 = findViewById(R.id.password2);

        username = usernameEditText.getText().toString();
        password1 = passwordEditText1.getText().toString();
        password2 = passwordEditText2.getText().toString();

        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(checkPassword() == true){
                    Intent intent = new Intent(SignupActivity.this, PopupActivity.class);
                    intent.putExtra("code", PopupActivity.SIGNUP_POPUP);
                    intent.putExtra("guide", "회원가입 성공");
                    startActivity(intent);
                }
                else{
                    passwordEditText1.getBackground().setColorFilter(Color.parseColor("ff0000"), PorterDuff.Mode.SRC);
                }
            }
        });
    }

    public boolean checkPassword(){
        if(password1.equals(password2)){
            return true;
        }
        else return false;
    }
}
