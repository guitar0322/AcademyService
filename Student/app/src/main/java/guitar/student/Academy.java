package guitar.student;

import java.io.Serializable;
import java.util.Date;

public class Academy implements Serializable {
    String name;
    String phone;
    int courseID;
    String courseName;
    String driverName;
    String driverPhone;
    double driverLatitude;
    double driverLongitude;
    Date arriveTime;
    boolean status;
    int busID;

    public Academy(String name, String phone, int courseID, String courseName, String driverName, String driverPhone, String status, int busID){
        this.name = name;
        this.phone = phone;
        this.courseID = courseID;
        this.courseName = courseName;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.busID = busID;
        if(status.equals("Y"))
            this.status = true;
        else
            this.status = false;
    }

    public void setLocation(double latitude, double longitude){
        driverLatitude = latitude;
        driverLongitude = longitude;
    }

    public void setArriveTime(Date arriveTime){
        this.arriveTime = arriveTime;
    }
}
