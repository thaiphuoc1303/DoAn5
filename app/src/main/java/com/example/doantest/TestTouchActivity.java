package com.example.doantest;

import static android.graphics.Color.*;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class TestTouchActivity extends AppCompatActivity
//        implements GestureDetector.OnGestureListener,
//        GestureDetector.OnDoubleTapListener
{
    LinearLayout layout;
    Mat matOrigin, matMark;

    SubsamplingScaleImageView imgImageView;
    int imgX, imgY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_touch);
        layout = findViewById(R.id.layout);
//        layout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                Log.e("1Touch", motionEvent.getX()+"");
//                Log.e("1Touch", motionEvent.getY()+"");
//                return false;
//            }
//        });
        imgImageView = findViewById(R.id.imgview);
        Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/IMG_20220412_105635.jpg");

        matOrigin = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4, new Scalar(255,0,0,90));
        matMark = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Scalar scalar = new Scalar(255, 0,0,255);
        Utils.bitmapToMat(bitmap, matOrigin);
        Imgproc.circle(matMark, new Point(300, 300), 50, scalar, 100);
        Imgproc.circle(matMark, new Point(150, 100), 50, scalar, 100);
        Core.addWeighted( matOrigin, 1, matMark, 0.5, 0.0, matOrigin);

//        matOrigin+=matMark;
        Utils.matToBitmap(matOrigin, bitmap);
        imgImageView.setImage(ImageSource.bitmap(bitmap));
        imgX = bitmap.getWidth();
        imgY = bitmap.getHeight();
        Log.e("Max", imgX+":"+imgY);
        imgImageView.setDoubleTapZoomScale(1);

        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (imgImageView.isReady()) {
                    PointF sCoord = imgImageView.viewToSourceCoord(e.getX(), e.getY());
                    if(sCoord.x<0 || sCoord.x > imgX || sCoord.y < 0 || sCoord.y > imgY){
                        return false;
                    }
                    Log.e("Point", sCoord.x+", "+sCoord.y+" getX: "+ imgImageView.getX()+ " getpivotX: "+imgImageView.getPivotX()
                    +" GetScale: "+imgImageView.getScale()+" Center: "+ imgImageView.getCenter());
                }
                return true;
            }
        });
        imgImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }
//
//    @Override
//    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
//        Log.e("1Touch", motionEvent.getX()+"");
//        Log.e("1Touch", motionEvent.getY()+"");
//        return false;
//    }
//
//    @Override
//    public boolean onDoubleTap(MotionEvent motionEvent) {
//        Log.e("2Touch", motionEvent.getX()+"");
//        Log.e("2Touch", motionEvent.getY()+"");
//
//        return false;
//    }
//
//    @Override
//    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
//        return false;
//    }
//
//    @Override
//    public boolean onDown(MotionEvent motionEvent) {
//        return false;
//    }
//
//    @Override
//    public void onShowPress(MotionEvent motionEvent) {
//
//    }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent motionEvent) {
//        return false;
//    }
//
//    @Override
//    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
//        return false;
//    }
//
//    @Override
//    public void onLongPress(MotionEvent motionEvent) {
//
//    }
//
//    @Override
//    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
//        return false;
//    }
}