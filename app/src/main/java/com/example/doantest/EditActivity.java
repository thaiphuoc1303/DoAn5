package com.example.doantest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditActivity extends AppCompatActivity {

    ImageButton btnUndo, btnRedo, btnCrop, btnContrast, btnText, btnBlur, btnBrightness, btnSave, btnClose, btnFilter;
    String link;
    Bitmap bitmap_origin;
    SubsamplingScaleImageView sImageView;
    PhotoLab mainPhoto;
    int buttonControl=0;
    Fragment fragment;
    LinearLayout layoutControl;
    int dialogStt;
    int size =5;

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

        sImageView.setImage(ImageSource.bitmap(mainPhoto.getBitMap()));


    }
    void init(){
        btnCrop = (ImageButton) findViewById(R.id.btnCrop);
        btnBrightness = findViewById(R.id.btnBrightness);
        btnContrast = findViewById(R.id.btnContrast);
        btnText = findViewById(R.id.btnText);
        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
        btnBlur = findViewById(R.id.btnBlur);
        btnSave = findViewById(R.id.btnSave);
        btnClose = findViewById(R.id.btnClose);
        btnFilter = findViewById(R.id.btnFilter);
        sImageView = (SubsamplingScaleImageView) findViewById(R.id.imgview);
        layoutControl = findViewById(R.id.controlview);

        // event
        btnFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = mainPhoto.filterA(EditActivity.this);
                        dialog.show();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                setImageViewMain(mainPhoto.getBitMap());
                            }
                        });
                    }
                });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = Environment.getExternalStorageDirectory() + File.separator + "Documents/PhotoLab/";

                File file = new File(link);
                if(!file.exists()){
                    file.mkdirs();
                }
                link +="draft.ptl";
                file = new File(link);
                file.delete();
                Log.e("Ok", link);
                try {
                    file.createNewFile();
                    Bitmap saveBitmap = mainPhoto.getBitMap();
//Convert bitmap to byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , bos); // YOU can also save it in JPEG

                    byte[] bitmapdata = bos.toByteArray();
//write the bytes in file
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                    Intent intent = new Intent(EditActivity.this, SaveActivity.class);
                    intent.putExtra("link", link);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
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
        btnBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog =mainPhoto.diaLogBlur(EditActivity.this, mainPhoto.matNow);
                dialog.show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        setImageViewMain(mainPhoto.getBitMap());
                    }
                });
            }
        });
    }
    public void setImageViewMain(Bitmap bitmap, SubsamplingScaleImageView scaleImageView){
        PointF pointF = scaleImageView.getCenter();
        float scale = scaleImageView.getScale();
        scaleImageView.setImage(ImageSource.bitmap(bitmap));
        scaleImageView.setScaleAndCenter(scale, pointF);
    }
    public void setImageViewMain(Bitmap bitmap){
        PointF pointF = sImageView.getCenter();
        float scale = sImageView.getScale();
        sImageView.setImage(ImageSource.bitmap(bitmap));
        sImageView.setScaleAndCenter(scale, pointF);
    }
}