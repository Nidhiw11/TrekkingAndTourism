package com.example.trek_tour;

public class User {
    public  String checkIn,checkOut,payment,accType ,tranId;

    //Constructor
    public User(){}


    public User(String CheckIn,String CheckOut, String Payment, String AType,String TId) {
        this.checkIn=CheckIn;
        this.checkOut=CheckOut;
        this.payment= Payment;
        this.accType= AType;
        this.tranId=TId;
    }
}

