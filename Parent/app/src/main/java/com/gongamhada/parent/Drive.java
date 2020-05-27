package com.gongamhada.parent;

public class Drive {
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

    public Drive(int academyID, String academyName, String academyPhone,
                 int courseID, String courseName,
                 int driverID, String driverName, String driverPhone, String status, int busID){
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
