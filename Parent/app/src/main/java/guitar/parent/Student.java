package guitar.parent;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Student implements Serializable {
    String name;
    String phone;
    String driverName;
    String driverPhone;
    double driverLatitude;
    double driverLongitude;
    double studentLatitude;
    double studentLongitude;
    Date arriveTime;
    String academyName;
    String academyPhone;
    String courseName;
    boolean status;

    public Student(String name, String phone, String academyName, String academyPhone, String courseName, String driverName, String driverPhone, boolean status ){
        this.name = name;
        this.phone = phone;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.academyName = academyName;
        this.academyPhone = academyPhone;
        this.courseName = courseName;
        this.status = status;
    }
}
