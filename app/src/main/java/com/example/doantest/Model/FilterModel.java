package com.example.doantest.Model;

import android.graphics.Bitmap;

public class FilterModel {
    Bitmap bitmap, nBitmap;
    String name;
    int pos;

    public FilterModel(Bitmap bitmap, String name, int pos) {
        this.bitmap = bitmap;
        this.name = name;
        this.pos = pos;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getnBitmap() {
        return nBitmap;
    }

    public void setnBitmap(Bitmap nBitmap) {
        this.nBitmap = nBitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
