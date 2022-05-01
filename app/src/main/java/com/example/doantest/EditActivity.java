package com.example.doantest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditActivity extends AppCompatActivity {

    ImageButton btnUndo, btnRedo, btnCrop, btnContrast, btnText, btnBlur, btnBrightness, btnSave, btnClose;
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
        btnBlur = findViewById(R.id.btnBlur);
        btnSave = findViewById(R.id.btnSave);
        btnClose = findViewById(R.id.btnClose);
        sImageView = (SubsamplingScaleImageView) findViewById(R.id.imgview);
        layoutControl = findViewById(R.id.controlview);

        // event
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Environment.getExternalStorageDirectory() + File.separator +"Pictures"
                String link = Environment.getExternalStorageDirectory() + File.separator + "Documents/PhotoLab/";

                File file = new File(link);
                if(!file.exists()){
                    file.mkdirs();
                }
                link +="draft.ptl";
                file = new File(link);
                file.delete();
//                Imgcodecs.imwrite(link, mainPhoto.matNow);
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
//                Dialog dialog = new Dialog(EditActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//                dialog.setContentView(R.layout.dialog_bokeh);
//                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialogInterface) {
//
//                    }
//                });
//                dialogStt =0;
//                ImageButton btnSelect, btnRemove, btnAction, btnDone;
//                LinearLayout layoutSize = dialog.findViewById(R.id.controlview);
//                btnSelect = dialog.findViewById(R.id.btnSelect);
//                btnRemove = dialog.findViewById(R.id.btnRemoveSelect);
//                btnAction = dialog.findViewById(R.id.btnAction);
//                SeekBar seekBarSize = dialog.findViewById(R.id.seekbarSize);
//                TextView tvValue = dialog.findViewById(R.id.tvValue);
//                btnDone = dialog.findViewById(R.id.btnDone);
//
//                SubsamplingScaleImageView scaleImageView = dialog.findViewById(R.id.imgview);
//                Mat matOrigin = new Mat(mainPhoto.matNow.height(), mainPhoto.matNow.width(), CvType.CV_8UC4);
//                mainPhoto.matNow.copyTo(matOrigin);
//
//                Mat newMat = new Mat(mainPhoto.matNow.height(), mainPhoto.matNow.width(), CvType.CV_8UC4);
//                matOrigin.copyTo(newMat);
//                Bitmap bitmap = Bitmap.createBitmap(matOrigin.cols(), matOrigin.rows(), Bitmap.Config.RGB_565);
//                Mat matMark = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
//                Mat matEx = Mat.zeros(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
//                Utils.matToBitmap(matOrigin, bitmap);
//                setImageViewMain(bitmap, scaleImageView);
//                Scalar scalar = new Scalar(255, 0,0,255);
//                Scalar scalarRemove = new Scalar(0, 0,0,255);
//                Scalar scalar1 = new Scalar(255);
//                GestureDetector gestureDetector = new GestureDetector(EditActivity.this, new GestureDetector.SimpleOnGestureListener() {
//                    @Override
//                    public boolean onSingleTapConfirmed(MotionEvent e) {
//                        int imgX = bitmap.getWidth();
//                        int imgY = bitmap.getHeight();
//                        if (scaleImageView.isReady()) {
//                            PointF sCoord = scaleImageView.viewToSourceCoord(e.getX(), e.getY());
//                            if(sCoord.x<0 || sCoord.x > imgX || sCoord.y < 0 || sCoord.y > imgY){
//                                return false;
//                            }
//                            if(dialogStt ==0) return false;
//                            if (dialogStt == R.id.btnSelect){
//                                Imgproc.circle(matMark, new Point(sCoord.x, sCoord.y), size, scalar, 2*size);
//                                Imgproc.circle(matEx, new Point(sCoord.x, sCoord.y), size, scalar1, 2*size);
//                                Mat matReview = null;
//                                matOrigin.copyTo(matReview);
//                                Core.addWeighted( matReview, 1, matMark, 1, 1, matReview);
//                                Bitmap bitmapReview = Bitmap.createBitmap(matOrigin.cols(), matOrigin.rows(), Bitmap.Config.RGB_565);
//                                Utils.matToBitmap(matReview, bitmapReview);
//                                setImageViewMain(bitmapReview, scaleImageView);
//                            }
//                            if (dialogStt == R.id.btnRemoveSelect){
//                                Imgproc.circle(matMark, new Point(sCoord.x, sCoord.y), size, scalarRemove, 2*size);
//                                Mat matReview = matOrigin;
//                                Core.addWeighted( matReview, 1, matMark, 0.1, 0, matReview);
//                                Bitmap bitmapReview = Bitmap.createBitmap(matOrigin.cols(), matOrigin.rows(), Bitmap.Config.RGB_565);
//                                Utils.matToBitmap(matReview, bitmapReview);
//                                setImageViewMain(bitmapReview, scaleImageView);
//                            }
//                        }
//                        return true;
//                    }
//
//                });
//                scaleImageView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//                        return gestureDetector.onTouchEvent(motionEvent);
//                    }
//                });
//                seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                        tvValue.setText(seekBar.getProgress()+"");
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//                        size = seekBar.getProgress();
//                    }
//                });
//                btnSelect.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (dialogStt == R.id.btnSelect){
//                            layoutSize.setVisibility(View.GONE);
//                            dialogStt = 0 ;
//                            return;
//                        }
//                        dialogStt = R.id.btnSelect;
//                        layoutSize.setVisibility(View.VISIBLE);
//                    }
//                });
//                btnRemove.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (dialogStt == R.id.btnRemoveSelect){
//                            layoutSize.setVisibility(View.GONE);
//                            dialogStt = 0 ;
//                            return;
//                        }
//                        dialogStt = R.id.btnRemoveSelect;
//                        layoutSize.setVisibility(View.VISIBLE);
//                    }
//                });
//                btnAction.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Bitmap bm = Bitmap.createBitmap(matOrigin.width(), matOrigin.height(), Bitmap.Config.RGB_565);
//                        Utils.matToBitmap(matOrigin, bm);
//                        setImageViewMain(bm, scaleImageView);
////                        scaleImageView.setImage(ImageSource.bitmap(bm));
//                        int k = 1;
//                        int maxX = matOrigin.width();
//                        int maxY = matOrigin.height();
//                        int channel = matEx.channels();
//                        Log.e("size", maxX+"_"+maxY);
//                        PhotoLabMatrix matrix = new PhotoLabMatrix();
//                        Mat newMat1 = new Mat(maxY, maxX, CvType.CV_8UC4);
//                        Bitmap newBitmap = Bitmap.createBitmap(newMat1.width(), newMat1.height(), Bitmap.Config.RGB_565);
//                        AsyncTask task = new AsyncTask() {
//                            @Override
//                            protected void onPreExecute() {
//                                super.onPreExecute();
//                                Log.e("aaa", "dang xu ly");
//                                for(int i = 1; i<maxY-1; i++){
//                                    for(int j = 1; j<maxX-1; j++){
//                                        double[] px = matEx.get(i, j);
//                                        if(px[0] > 100){
//                                            PhotoLabPixel[][] plP = {{new PhotoLabPixel(matOrigin.get(i-1, j-1)), new PhotoLabPixel(matOrigin.get(i-1, j)), new  PhotoLabPixel(matOrigin.get(i-1, j+1))},
//                                                    {new PhotoLabPixel(matOrigin.get(i, j-1)), new PhotoLabPixel(matOrigin.get(i, j)), new PhotoLabPixel(matOrigin.get(i, j+1))},
//                                                    {new PhotoLabPixel(matOrigin.get(i+1, j-1)), new PhotoLabPixel(matOrigin.get(i+1, j)), new PhotoLabPixel(matOrigin.get(i+1, j+1))}};
//                                            newMat1.put(i, j, matrix.MedianBlur(plP));
//                                            Log.e("pixel", matOrigin.get(i,j)[1]+","+newMat1.get(i,j)[1]);
//                                        }
//                                        else{
//                                            newMat1.put(i, j, matOrigin.get(i, j));
//                                        }
//
//                                    }
//                                }
//                            }
//
//                            @Override
//                            protected void onPostExecute(Object o) {
//                                super.onPostExecute(o);
//                                Utils.matToBitmap(newMat1, newBitmap);
//                                Bitmap abc = Bitmap.createBitmap(newMat1.width(), newMat1.height(), Bitmap.Config.RGB_565);
//                                Utils.matToBitmap(newMat1, abc);
//                                scaleImageView.setImage(ImageSource.bitmap(abc));
//                                newMat1.copyTo(newMat);
//
//                            }
//
//                            @Override
//                            protected Object doInBackground(Object[] objects) {
//                                return null;
//                            }
//                        };
//                        task.execute();
//                        Bitmap abc = Bitmap.createBitmap(newMat.width(), newMat.height(), Bitmap.Config.RGB_565);
//                        Utils.matToBitmap(newMat, abc);
//                        scaleImageView.setImage(ImageSource.bitmap(abc));
//                    }
//                });
//                btnDone.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mainPhoto.add(newMat);
//                        sImageView.setImage(ImageSource.bitmap(mainPhoto.getBitMap()));
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();
                mainPhoto.diaLogBlur(EditActivity.this, mainPhoto.matNow).show();
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