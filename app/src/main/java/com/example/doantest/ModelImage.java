package com.example.doantest;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ModelImage implements Serializable {
    Bitmap anhGoc, anhTienTai, anhUndo, anhRedo;
    int x, y;
    public ModelImage(Bitmap anhGoc){
        this.anhGoc = anhGoc;
        anhTienTai = anhGoc;
    }
}
