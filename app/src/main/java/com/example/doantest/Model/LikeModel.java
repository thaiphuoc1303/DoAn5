package com.example.doantest.Model;

public class LikeModel {
    long time;
    String uID, name;

    public LikeModel(long time, String uID, String name) {
        this.time = time;
        this.uID = uID;
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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

    public LikeModel() {
    }
}