package com.gongamhada.driver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s){
        super.onNewToken(s);
        Log.e("firebase_test", "FirebaseInstanceID = " + s);
        SharedPreferences preferences = getSharedPreferences("DeviceToken", MODE_PRIVATE);;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", s);
        editor.commit();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.d("firebase_test", "message received : " + remoteMessage.toString());
        if(remoteMessage != null && remoteMessage.getData().size() > 0){
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage){
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String message;
        String acaName= "";
        String acaSub = "";
        int acaNo = 0;
        int drvNo = 0;

        Log.d("firebase_test", "result = " + acaName + acaSub + acaNo);
        Log.d("firebase_test", "From = " + remoteMessage.getFrom());
        Log.d("firebase_test", "Message data payload = " + remoteMessage.getData());

        try{
            JSONArray jsonArray = new JSONArray(body);
            acaName = jsonArray.getJSONObject(0).getString("name");
            if(jsonArray.getJSONObject(0).getString("subName").equals("NULL"))
                acaSub = "";
            acaNo = jsonArray.getJSONObject(0).getInt("acaNo");
            drvNo = jsonArray.getJSONObject(0).getInt("drvNo");
        }
        catch(Exception e){
            e.printStackTrace();
        }

        if(acaSub.equals(""))
            message = acaName + "의 등록요청이 도착 하였습니다";
        else
            message = acaName + " " + acaSub + "의 등록요청이 도착 하였습니다";

        Intent intent = new Intent(this, AcceptActivity.class);
        intent.putExtra("name", acaName);
        intent.putExtra("acaSub", acaSub);
        intent.putExtra("acaNo", acaNo);
        intent.putExtra("drvNo", drvNo);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        /**
         * 오레오 버전부터는 Notification Channel 필수.
         * **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channel = "채널";
            String channel_nm = "채널명";

            NotificationManager notichannel = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channelMessage = new NotificationChannel(channel, channel_nm,
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            channelMessage.setDescription("채널에 대한 설명.");
            channelMessage.enableLights(true);
            channelMessage.enableVibration(true);
            channelMessage.setShowBadge(false);
            channelMessage.setVibrationPattern(new long[]{100, 200, 100, 200});
            notichannel.createNotificationChannel(channelMessage);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channel)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setChannelId(channel)
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());

        } else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, "")
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());

        }
    }
}
