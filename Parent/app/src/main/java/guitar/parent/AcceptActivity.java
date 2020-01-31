package guitar.parent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class AcceptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept);
        Intent intent = new Intent(this, ChoicePopupActivity.class);
        intent.putExtra("guide", "ooo학원의 등록요청입니다. 동의하시겠습니까?");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 1:
                if(resultCode == RESULT_OK) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    Log.d("accept_test", "accept");
                }
                else{
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                break;
        }
    }
}
