package com.gongamhada.student;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DriverInfoAdapter implements GoogleMap.InfoWindowAdapter {
    View window;
    Academy driverInfo;
    public DriverInfoAdapter(View window, Academy driverInfo){
        this.window = window;
        this.driverInfo = driverInfo;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView driverName = window.findViewById(R.id.driverName);
        TextView driverPhone = window.findViewById(R.id.driverPhone);
        TextView arrivetime = window.findViewById(R.id.arriveTime);

        driverName.setText(driverInfo.driverName);
        driverPhone.setText(driverInfo.driverPhone);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmm", Locale.KOREA);
        String  now = simpleDateFormat.format(System.currentTimeMillis());
        Date nowDate = null;
        Date arriveDate = null;
        try {
            nowDate = simpleDateFormat.parse(now);
            arriveDate = simpleDateFormat.parse(driverInfo.arriveTime);
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d("s_time_test", "" + now + " , " + driverInfo.arriveTime);
        int min = (int)(arriveDate.getTime() - nowDate.getTime())/60000;
        arrivetime.setText(min + "분후 도착합니다");
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
