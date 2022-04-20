package com.example.doantest;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ModelImage implements Serializable {
    Bitmap anhGoc, anhHienTai, anhUndo, anhRedo;
    int x, y;
    public ModelImage(Bitmap anhGoc){
        this.anhGoc = anhGoc;
        anhHienTai = anhGoc;
    }

    public Bitmap getAnhGoc() {
        return anhGoc;
    }

    public void setAnhGoc(Bitmap anhGoc) {
        this.anhGoc = anhGoc;
    }

    public Bitmap getAnhHienTai() {
        return anhHienTai;
    }

    public void setAnhHienTai(Bitmap anhHienTai) {
        this.anhHienTai = anhHienTai;
    }
}
