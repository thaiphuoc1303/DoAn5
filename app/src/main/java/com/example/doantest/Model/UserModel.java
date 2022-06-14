package com.example.doantest.Model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class UserModel implements Serializable {
    String uID, name;

    public UserModel(){}
    public UserModel(String uID, String name) {
        this.uID = uID;
        this.name = name;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
