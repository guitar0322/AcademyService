package guitar.academyservice;

import com.skt.Tmap.TMapPoint;

import java.io.Serializable;
import java.util.ArrayList;

public class Point implements Serializable {
    int id;
    String name;
    double latitude;
    double longitude;
    boolean check;
    ArrayList<Student> studentList;

    public Point(int id, double latitude, double longitude, String _name, ArrayList<Student> _studentList) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = _name;
        this.studentList = _studentList;
        this.check = false;
    }
}
