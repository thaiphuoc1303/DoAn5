package com.example.doantest.Interface;

import android.graphics.Bitmap;

import com.example.doantest.Model.ImageModel;

public interface ClickItemListener {
    void ClickItemFilter(Bitmap bitmap, int pos);
    void ClickItemImage(ImageModel imageModel);
}
