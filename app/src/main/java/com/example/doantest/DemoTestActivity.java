package com.example.doantest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class DemoTestActivity extends AppCompatActivity {
    ImageView imageView;
    ImageButton btnCrop;
    ModelImage image;
    String link;
    Bitmap bitmap_origin;
    SubsamplingScaleImageView scaleImageView;
    SeekBar sbBrightness;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_test);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        init();
        Intent intent = getIntent();
        link = (String) intent.getStringExtra("img");
        bitmap_origin = BitmapFactory.decodeFile(link);
        Mat image = new Mat(bitmap_origin.getHeight(), bitmap_origin.getWidth(), CvType.CV_8U, new Scalar(4));
        Utils.bitmapToMat(bitmap_origin, image);

        scaleImageView.setImage(ImageSource.bitmap(bitmap_origin));
        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
    void init(){
        btnCrop = (ImageButton) findViewById(R.id.btnCrop);
        scaleImageView = (SubsamplingScaleImageView) findViewById(R.id.imgview);
        sbBrightness = findViewById(R.id.seekBar);
        sbBrightness.setMax(100);
        sbBrightness.setMin(-100);
        sbBrightness.setProgress(0);
    }
}