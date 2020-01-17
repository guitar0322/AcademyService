package guitar.student;

import java.io.Serializable;
import java.util.Date;

public class Academy implements Serializable {
    String name;
    String phone;
    String courseName;
    String driverName;
    String driverPhone;
    double driverLatitude;
    double driverLongitude;
    Date arriveTime;
    boolean status;

    public Academy(String name, String phone, String courseName, String driverName, String driverPhone, boolean status){
        this.name = name;
        this.phone = phone;
        this.courseName = courseName;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.status = status;
    }

    public void setLocation(double latitude, double longitude){
        driverLatitude = latitude;
        driverLongitude = longitude;
    }

    public void setArriveTime(Date arriveTime){
        this.arriveTime = arriveTime;
    }
}
