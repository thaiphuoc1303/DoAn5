package com.example.doantest.Model;

import android.graphics.Bitmap;

public class ImageModel {
    Bitmap bitmap;
    String name, link;

    public ImageModel(Bitmap bitmap, String name, String link) {
        this.bitmap = bitmap;
        this.name = name;
        this.link = link;
    }

    public ImageModel(Bitmap bitmap, String name) {
        this.bitmap = bitmap;
        this.name = name;
    }

    public ImageModel(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
