package guitar.student;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import org.json.JSONObject;

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "AccessimilityService";
    String blockURL;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        boolean denyApp = false;
        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            if("com.android.settings".equals(event.getPackageName())){
//                if(event.getText().get(0).equals("AcademyService")){
//                    denyApp = true;
////                    gotoHome();
//                }
//            }

            if( "guitar.parent".equals(event.getPackageName())) {
                denyApp = true;
                blockURL = getString(R.string.url) + "stdnt/block?";
                ContentValues contentValues = new ContentValues();
                contentValues.put("stdntNo", getSharedPreferences("AutoLogin", MODE_PRIVATE).getInt("id", 0));
                RequestBlockTask requestBlockTask = new RequestBlockTask(blockURL, contentValues);
                requestBlockTask.execute();
            }

            if(denyApp == true){
                Log.e(TAG,
                        "Window Package: " + event.getPackageName());
                Log.e(TAG, "Window Source : " + event.getSource());
                Log.e(TAG, "Catch Event TEXT : " + event.getText());
                denyApp = false;
            }
//            Log.e(TAG, "Catch Event Package Name : " + event.getPackageName());
//            Log.e(TAG, "Catch Event TEXT : " + event.getText());
//            Log.e(TAG, "Catch Event ContentDescription : " + event.getContentDescription());
//            Log.e(TAG, "Catch Event getSource : " + event.getSource());
//            Log.e(TAG, "=========================================================================");
        }
    }

    public class RequestBlockTask extends AsyncTask {
        private String url;
        private ContentValues values;

        public RequestBlockTask(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }
        @Override
        protected void onPreExecute(){
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            String result;
            HttpClient httpClient = new HttpClient();
            result = httpClient.request(url, values);
            if(result == "" || result == null){
                result = "testresult";
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d(TAG, o.toString());
            parseResult(o.toString());
           //Todo after httpNetworking.
            //ex)Intent, terminate progress, courselist setting
        }
    }

    public void parseResult(String result){
        Log.d("access_test", "access result = " + result);
        JSONObject jsonResult;
        try{
            jsonResult = new JSONObject(result);
            if(jsonResult.getString("status").equals("Y")){
                Toast.makeText(this, "앱이 거부되었습니다", Toast.LENGTH_SHORT).show();
                gotoHome();
            }
        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "앱이 거부되었습니다", Toast.LENGTH_SHORT).show();
        }
    }

//    public void onServiceConnected(){
//        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
//
//        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK; // 전체 이벤트 가져오기
//        info.feedbackType = AccessibilityServiceInfo.DEFAULT | AccessibilityServiceInfo.FEEDBACK_HAPTIC;
//        info.notificationTimeout = 100; // millisecond
//
//        setServiceInfo(info);
//    }

    @Override
    public void onInterrupt() {
        Log.e("TEST", "OnInterrupt");
    }

    private void gotoHome(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                | Intent.FLAG_ACTIVITY_FORWARD_RESULT
                | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }
}
