package com.example.doantest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;

public class PhotoLab {
    Mat matOrigin, matNow;
    ArrayList<Mat>  mats;
    int now, intBrightness=0;
    String link;


    public PhotoLab(String link) {
        this.link = link;
        Bitmap bitmap = BitmapFactory.decodeFile(link);
        Mat image = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8U, new Scalar(4));
        Utils.bitmapToMat(bitmap, image);
        matOrigin = image;
        matNow = matOrigin;
        mats = new ArrayList<Mat>();
        mats.add(matNow);

        now = 0;
    }
    void add(Mat mat){
        remove();
        matNow = mat;
        mats.add(mat);
        now++;
        if(mats.size()>7) {
            mats.remove(0);
            now--;
        }
    }
    void remove(){
        while(mats.size()>now+1){
            mats.remove(now+1);
        }
    }
    public Bitmap Brightness(int n){
        n = n - intBrightness;
        intBrightness = n;
        mats.get(now).convertTo(matNow, -1, 1, n);
        remove();
        add(matNow);
        now++;
        Bitmap bitmap = Bitmap.createBitmap(matNow.cols(), matNow.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(matNow, bitmap);
        return bitmap;
    }
    public Bitmap getBitMap(){
        Bitmap bitmap = Bitmap.createBitmap(matNow.cols(), matNow.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(matNow, bitmap);
        return bitmap;
    }
}
