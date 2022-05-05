package com.example.doantest.Model;

import android.graphics.Bitmap;
import android.widget.ImageButton;

import java.util.Date;

public class StatusModel {
    String name, strImg, id, stt;
    Date time;
    Bitmap img;

    public StatusModel(String name, String strImg, String id, Date time, Bitmap img) {
        this.name = name;
        this.strImg = strImg;
        this.id = id;
        this.time = time;
        this.img = img;
    }

    public StatusModel(String name, String strImg, String id, String stt, Date time, Bitmap img) {
        this.name = name;
        this.strImg = strImg;
        this.id = id;
        this.stt = stt;
        this.time = time;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStrImg() {
        return strImg;
    }

    public void setStrImg(String strImg) {
        this.strImg = strImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }
}
