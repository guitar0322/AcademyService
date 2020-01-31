package guitar.parent;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Student implements Serializable {
    int id;
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
    Date arriveTime;
    String academyName;
    String academyPhone;
    int courseID;
    String courseName;
    boolean status;

    public Student(int id, String name, String phone, String block,
                   String academyName, String academyPhone,
                   int courseID, String courseName,
                   int driverID, String driverName, String driverPhone, String status ){
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

        this.academyName = academyName;
        this.academyPhone = academyPhone;

        this.courseID = courseID;
        this.courseName = courseName;
        if(status.equals("Y"))
            this.status = true;
        else
            this.status = false;
    }
}
