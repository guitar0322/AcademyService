package com.gongamhada.student;

import java.io.Serializable;
import java.util.Date;

public class Academy implements Serializable {
    int id;
    String name;
    String phone;
    int courseID;
    String courseName;
    String driverName;
    String driverPhone;
    double driverLatitude;
    double driverLongitude;
    String arriveTime;
    boolean status;
    int busID;

    public Academy(int id, String name, String phone, int courseID, String courseName, String driverName, String driverPhone, String status, int busID){
        this.id = id;
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

    public void setArriveTime(String arriveTime){
        this.arriveTime = arriveTime;
    }
}
