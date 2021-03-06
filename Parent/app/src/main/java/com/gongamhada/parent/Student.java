package com.gongamhada.parent;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class Student implements Serializable {
    int id;
    int tracking_id;
    String name;
    String phone;
    boolean block;
    int driverID;
    String driverName;
    String driverPhone;
    double driverLatitude;
    double driverLongitude;
    double studentLatitude;
    double studentLongitude;
    String arriveTime;
    int academyID;
    String academyName;
    String academyPhone;
    int courseID;
    String courseName;
    boolean status;
    int busID;

    public Student(int id, int tracking_id, String name, String phone, String block,
                   int academyID, String academyName, String academyPhone,
                   int courseID, String courseName,
                   int driverID, String driverName, String driverPhone, String status, int busID){
        this.tracking_id = tracking_id;
        this.id = id;
        this.name = name;
        this.phone = phone;
        if(block.equals("Y"))
            this.block = true;
        else
            this.block = false;
        this.driverID = driverID;
        this.driverName = driverName;
        this.driverPhone = driverPhone;

        this.academyID = academyID;
        this.academyName = academyName;
        this.academyPhone = academyPhone;

        this.courseID = courseID;
        this.courseName = courseName;
        if(status.equals("Y"))
            this.status = true;
        else
            this.status = false;
        this.busID = busID;
    }
}
