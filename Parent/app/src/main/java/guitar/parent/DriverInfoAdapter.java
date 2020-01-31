package guitar.parent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

public class DriverInfoAdapter implements GoogleMap.InfoWindowAdapter {
    View window;
    Student student;
    public DriverInfoAdapter(View window, Student student){
        this.window = window;
        this.student = student;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView driverName = window.findViewById(R.id.driverName);
        TextView driverPhone = window.findViewById(R.id.driverPhone);
        TextView arrivetime = window.findViewById(R.id.arriveTime);

        driverName.setText(student.driverName);
        driverPhone.setText(student.driverPhone);
        arrivetime.setText(student.arriveTime.toString());
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
