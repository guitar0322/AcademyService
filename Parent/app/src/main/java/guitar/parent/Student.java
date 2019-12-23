package guitar.parent;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Student implements Serializable {
    String name;
    String driverName;
    String driverPhone;
    double driverLatitude;
    double driverLongitude;
    Date arriveTime;
    String academyName;
    String academyPhone;
    int status;

    public Student(String name, String driverName, String driverPhone, String academyName, String academyPhone){
        this.name = name;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.academyName = academyName;
        this.academyPhone = academyPhone;
    }
}
