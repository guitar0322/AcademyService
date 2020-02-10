package guitar.academyservice;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    int id;
    String name;
    int academyID;
    String academyName;
    String academyPhone;
    String academyAddress;
    ArrayList<Point> pointList;
    public ArrayList<Path> pathlist;
    int busID;

    public Course(int id, String name, int academyID, String academyName, String academyPhone, String academyAddress, ArrayList<Point> pointList, int busID){
        this.id = id;
        this.name = name;
        this.academyID = academyID;
        this.academyName = academyName;
        this.academyPhone = academyPhone;
        this.academyAddress = academyAddress;
        this.pointList = pointList;
        this.busID = busID;
        pathlist = new ArrayList<>();
    }
    public void addPath(double latitude, double longitude){
        pathlist.add(new Path(latitude, longitude));
    }
    public class Path implements Serializable{
        public Double latitude;
        public Double longitude;
        public Path(Double latitude, Double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
