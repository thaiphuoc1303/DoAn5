package com.example.doantest;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
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

import java.util.ArrayList;

public class PhotoLab {
    Mat matOrigin, matNow, matSelect;
    ArrayList<Mat>  mats;
    int now, intBrightness=0;
    String link;
    int size = 5;
    int sttDialog = 0;

    public PhotoLab(String link) {
        this.link = link;
        Bitmap bitmap = BitmapFactory.decodeFile(link);
        Mat image = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8U);
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
    public Dialog diaLogBlur(Context context, Mat origin){
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_bokeh);

        ImageButton btnSelect, btnRemove, btnAction, btnDone;
        LinearLayout layoutSize = dialog.findViewById(R.id.controlview);
        btnSelect = dialog.findViewById(R.id.btnSelect);
        btnRemove = dialog.findViewById(R.id.btnRemoveSelect);
        btnAction = dialog.findViewById(R.id.btnAction);
        SeekBar seekBarSize = dialog.findViewById(R.id.seekbarSize);
        TextView tvValue = dialog.findViewById(R.id.tvValue);
        btnDone = dialog.findViewById(R.id.btnDone);
        SubsamplingScaleImageView scaleImageView = dialog.findViewById(R.id.imgview);

        Mat mOrigin = new Mat(origin.height(), origin.width(), CvType.CV_8UC4);
        origin.copyTo(mOrigin);
        Mat newMat = new Mat(origin.height(), origin.width(), CvType.CV_8UC4);
        origin.copyTo(newMat);
        Bitmap bitmap = Bitmap.createBitmap(origin.cols(), origin.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(origin, bitmap);
        Mat matMark = new Mat(origin.height(), origin.width(), CvType.CV_8UC4);
        Mat mat1c = new Mat(origin.height(), origin.width(), CvType.CV_8UC1);
        scaleImageView.setImage(ImageSource.bitmap(bitmap));
        Scalar scalar = new Scalar(255, 0,0,255);
        Scalar scalarRemove = new Scalar(0, 0,0,255);
        Scalar scalar1 = new Scalar(255);
        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                int intX = origin.width();
                int intY = origin.height();
                if (scaleImageView.isReady()) {
                    PointF sCoord = scaleImageView.viewToSourceCoord(e.getX(), e.getY());
                    if(sCoord.x<0 || sCoord.x > intX || sCoord.y < 0 || sCoord.y > intY){
                        return false;
                    }
                    if(sttDialog ==0) return false;
                    if (sttDialog == R.id.btnSelect){
                        Imgproc.circle(matMark, new Point(sCoord.x, sCoord.y), size, scalar, 2*size);
                        Imgproc.circle(mat1c, new Point(sCoord.x, sCoord.y), size, scalar1, 2*size);
                        Mat matReview = new Mat(origin.height(), origin.width(), CvType.CV_8UC4);
                        origin.copyTo(matReview);
                        Core.addWeighted( matReview, 1, matMark, 1, 1, matReview);
                        Bitmap bitmapReview = Bitmap.createBitmap(intX, intY, Bitmap.Config.RGB_565);
                        Utils.matToBitmap(matReview, bitmapReview);
                        scaleImageView.setImage(ImageSource.bitmap(bitmapReview));

                    }
                }
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        add(newMat);
                        scaleImageView.setImage(ImageSource.bitmap(getBitMap()));
                        dialog.dismiss();
                    }
                });

                return true;
            }

        });
        scaleImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvValue.setText(seekBar.getProgress()+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                size = seekBar.getProgress();
            }
        });
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sttDialog == R.id.btnSelect){
                    layoutSize.setVisibility(View.GONE);
                    sttDialog = 0 ;
                    return;
                }
                sttDialog = R.id.btnSelect;
                layoutSize.setVisibility(View.VISIBLE);
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sttDialog == R.id.btnRemoveSelect){
                    layoutSize.setVisibility(View.GONE);
                    sttDialog = 0 ;
                    return;
                }
                sttDialog = R.id.btnRemoveSelect;
                layoutSize.setVisibility(View.VISIBLE);
            }
        });
                btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bitmap bm = Bitmap.createBitmap(matOrigin.width(), matOrigin.height(), Bitmap.Config.RGB_565);
                        Utils.matToBitmap(origin, bm);
                        scaleImageView.setImage(ImageSource.bitmap(bm));
                        int k = 1;
                        int maxX = matOrigin.width();
                        int maxY = matOrigin.height();
                        int channel = mat1c.channels();
                        PhotoLabMatrix matrix = new PhotoLabMatrix();
                        Mat newMat1 = new Mat(maxY, maxX, CvType.CV_8UC4);
                        Bitmap newBitmap = Bitmap.createBitmap(newMat1.width(), newMat1.height(), Bitmap.Config.RGB_565);
                        AsyncTask task = new AsyncTask() {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                Log.e("aaa", "dang xu ly");
                                for(int i = 1; i<maxY-1; i++){
                                    for(int j = 1; j<maxX-1; j++){
                                        double[] px = mat1c.get(i, j);
                                        if(px[0] > 100){
                                            PhotoLabPixel[][] plP = {{new PhotoLabPixel(origin.get(i-1, j-1)), new PhotoLabPixel(origin.get(i-1, j)), new  PhotoLabPixel(origin.get(i-1, j+1))},
                                                    {new PhotoLabPixel(origin.get(i, j-1)), new PhotoLabPixel(origin.get(i, j)), new PhotoLabPixel(origin.get(i, j+1))},
                                                    {new PhotoLabPixel(origin.get(i+1, j-1)), new PhotoLabPixel(origin.get(i+1, j)), new PhotoLabPixel(origin.get(i+1, j+1))}};
                                            newMat1.put(i, j, matrix.MedianBlur(plP));
                                            Log.e("pixel", origin.get(i,j)[1]+","+newMat1.get(i,j)[1]);
                                        }
                                        else{
                                            newMat1.put(i, j, origin.get(i, j));
                                        }

                                    }
                                }
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                super.onPostExecute(o);
                                Utils.matToBitmap(newMat1, newBitmap);
                                Bitmap abc = Bitmap.createBitmap(newMat1.width(), newMat1.height(), Bitmap.Config.RGB_565);
                                Utils.matToBitmap(newMat1, abc);
                                scaleImageView.setImage(ImageSource.bitmap(abc));
                                newMat1.copyTo(newMat);

                            }

                            @Override
                            protected Object doInBackground(Object[] objects) {
                                return null;
                            }
                        };
                        task.execute();
                        Bitmap abc = Bitmap.createBitmap(newMat.width(), newMat.height(), Bitmap.Config.RGB_565);
                        Utils.matToBitmap(newMat, abc);
                        scaleImageView.setImage(ImageSource.bitmap(abc));
                    }
                });
        return dialog;
    }
}
