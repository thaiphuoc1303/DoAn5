package com.example.doantest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class EditActivity extends AppCompatActivity {

    ImageButton btnUndo, btnRedo, btnCrop, btnContrast, btnText, btnBrightness;
    String link;
    Bitmap bitmap_origin;
    SubsamplingScaleImageView scaleImageView;
    PhotoLab mainPhoto;
    int buttonControl=0;
    Fragment fragment;
    LinearLayout layoutControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        init();
        Intent intent = getIntent();
        link = (String) intent.getStringExtra("link");

        mainPhoto = new PhotoLab(link);

//        Mat src = Imgcodecs.imread(link, Imgcodecs.IMREAD_COLOR);
//        bitmap_origin = BitmapFactory.decodeFile(link);
//        Mat image = new Mat(bitmap_origin.getHeight(), bitmap_origin.getWidth(), CvType.CV_8U, new Scalar(4));
//        Utils.bitmapToMat(bitmap_origin, image);

        scaleImageView.setImage(ImageSource.bitmap(mainPhoto.getBitMap()));
        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
    void init(){
        btnCrop = (ImageButton) findViewById(R.id.btnCrop);
        btnBrightness = findViewById(R.id.btnBrightness);
        btnContrast = findViewById(R.id.btnContrast);
        btnText = findViewById(R.id.btnText);
        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
        scaleImageView = (SubsamplingScaleImageView) findViewById(R.id.imgview);
        layoutControl = findViewById(R.id.controlview);

        // event
        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainPhoto.now>0){
                    mainPhoto.now--;
                    mainPhoto.matNow = mainPhoto.mats.get(mainPhoto.now);
                    setImageViewMain(mainPhoto.getBitMap());
                }
            }
        });
        btnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainPhoto.now<mainPhoto.mats.size()-1){
                    mainPhoto.now++;
                    mainPhoto.matNow = mainPhoto.mats.get(mainPhoto.now);
                    setImageViewMain(mainPhoto.getBitMap());
                }
            }
        });
        btnBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonControl==R.id.btnBrightness){
                    layoutControl.setVisibility(View.GONE);
                    buttonControl= 0;
                    return;
                }
                layoutControl.setVisibility(View.VISIBLE);
                mainPhoto.add(mainPhoto.mats.get(mainPhoto.now));
                fragment = new BrightnessFragment(mainPhoto.mats.get(mainPhoto.now));
                getSupportFragmentManager().beginTransaction().replace(R.id.controlview, fragment).commit();
                buttonControl=R.id.btnBrightness;
            }
        });
        btnContrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonControl == R.id.btnContrast){
                    layoutControl.setVisibility(View.GONE);
                    buttonControl= 0;
                    return;
                }
                layoutControl.setVisibility(View.VISIBLE);
                mainPhoto.add(mainPhoto.mats.get(mainPhoto.now));
                fragment = new ContrastFragment(mainPhoto.mats.get(mainPhoto.now));
                getSupportFragmentManager().beginTransaction().replace(R.id.controlview, fragment).commit();
                buttonControl=R.id.btnContrast;
            }
        });
    }
    public void setImageViewMain(Bitmap bitmap){
        scaleImageView.setImage(ImageSource.bitmap(bitmap));
    }
}