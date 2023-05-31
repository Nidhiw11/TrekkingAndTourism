package com.example.trek_tour;

public class ReadWriteDetails {
    public  String fullname,dob,gender,mobile;

    //Constructor
    public ReadWriteDetails(){}

    public ReadWriteDetails(String textFullName,String textDOB,String textGender,String textMobile){
        this.fullname=textFullName;
        this.dob=textDOB;
        this.gender=textGender;
        this.mobile=textMobile;
    }
}
