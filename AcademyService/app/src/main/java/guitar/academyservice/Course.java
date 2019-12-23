package guitar.academyservice;

import java.io.Serializable;
import java.util.ArrayList;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    ArrayList<Point> pointList;

    public Course(String _name, ArrayList<Point> _pointList){
        name = _name;
        pointList = _pointList;
    }
}
