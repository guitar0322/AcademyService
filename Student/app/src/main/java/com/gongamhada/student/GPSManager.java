package com.gongamhada.student;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class GPSManager extends Service implements LocationListener {
    Context context;
    Location location;//위치정보저장
    double latitude;
    double longitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 1000;
    protected LocationManager locationManager;

    public GPSManager(Context _context){
        context = _context;
        getLocation();
    }
    public Location getLocation(){
        try {

            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled == false && !isNetworkEnabled == false) {
                Log.d("gpsTest", "gps off");
            } else {
                //권한을 보유하고 있는지 검사
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

                //PERMISSION_GRANTED일 경우 권한허용 상태 PERMISSION_DENIED일 경우 거부상태
                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED ||
                        hasCoarseLocationPermission == PackageManager.PERMISSION_DENIED) {
                    //둘중 하나라도 거부상태일 경우 null을 리턴
                    Log.d("gpsTest", "permission denied");
                    return null;
                }


                //네트워크를 통해 gps정보를 얻고 만약 실패했을때 gps_provider를 통해 정보를 제공받음.
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null)
                    {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null)
                        {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled)
                {
                    if (location == null)
                    {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null)
                        {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null)
                            {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                        else{
                            Log.d("gpsTest", "null...");
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.d("locationError@@", ""+e.toString());
        }
        return location;
    }
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    @Override
    public void onLocationChanged(Location location){
        Log.d("gpsTest", "LocationChanged");

    }
    @Override
    public void onProviderDisabled(String provider){
        Log.d("gpsTest", "ProviderDisable");

    }
    @Override
    public void onProviderEnabled(String provider){
        Log.d("gpsTest", "ProviderAble");

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
        Log.d("gpsTest", "StatusChanged");
    }
    @Override
    public IBinder onBind(Intent arg0)
    {
        Log.d("gpsTest", "OnBind");
        return null;
    }
}
