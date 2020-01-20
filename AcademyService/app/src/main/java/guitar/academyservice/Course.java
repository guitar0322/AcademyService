package guitar.academyservice;

import java.io.Serializable;
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
    ArrayList<Path> pathlist;

    public Course(int id, String name, int academyID, String academyName, String academyPhone, String academyAddress, ArrayList<Point> pointList){
        this.id = id;
        this.name = name;
        this.academyID = academyID;
        this.academyName = academyName;
        this.academyPhone = academyPhone;
        this.academyAddress = academyAddress;
        this.pointList = pointList;
    }
    public void addPath(double latitude, double longitude){
        pathlist.add(new Path(latitude, longitude));
    }
    private class Path{
        Double latitude;
        Double longitude;
        public Path(Double latitude, Double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
