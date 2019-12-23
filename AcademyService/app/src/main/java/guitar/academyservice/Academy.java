package guitar.academyservice;

import java.io.Serializable;
import java.util.ArrayList;

public class Academy implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    ArrayList<Course> courseList;

    public Academy(String _name, ArrayList<Course> _courseList){
        name = _name;
        courseList = _courseList;
    }
}
