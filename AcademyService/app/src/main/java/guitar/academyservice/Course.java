package guitar.academyservice;

import java.io.Serializable;
import java.util.ArrayList;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    String academyName;
    String academyPhone;
    String academyAddress;
    ArrayList<Point> pointList;

    public Course(String name, String academyName, String academyPhone, String academyAddress, ArrayList<Point> pointList){
        this.name = name;
        this.academyName = academyName;
        this.academyPhone = academyPhone;
        this.academyAddress = academyAddress;
        this.pointList = pointList;
    }
}
