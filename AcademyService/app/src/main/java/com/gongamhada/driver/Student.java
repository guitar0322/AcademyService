package com.gongamhada.driver;

import java.io.Serializable;

public class Student implements Serializable {
    int id;
    int acc_id;
    String name;
    String phone;
    String parentName;
    String parentPhone;
    double latitude;
    double longitude;
    boolean check;

    public Student(int id, int acc_id, String _name, String _phone, String _parentPhone){
        this.id = id;
        this.acc_id = acc_id;
        name = _name;
        phone = _phone;
        parentPhone = _parentPhone;
    }

    public void setPosition(double latitude ,double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
