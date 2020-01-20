package guitar.academyservice;

import java.io.Serializable;

public class Student implements Serializable {
    int id;
    String name;
    String phone;
    String parentName;
    String parentPhone;
    double latitude;
    double longitude;

    public Student(int id, String _name, String _phone, String _parentName, String _parentPhone){
        this.id = id;
        name = _name;
        phone = _phone;
        parentName = _parentName;
        parentPhone = _parentPhone;
    }

    public void setPosition(double latitude ,double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
