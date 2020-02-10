package guitar.student;

import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

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
        arrivetime.setText(driverInfo.arriveTime.toString());
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
