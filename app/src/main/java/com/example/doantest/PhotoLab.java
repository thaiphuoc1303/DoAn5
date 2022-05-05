package com.example.doantest;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.doantest.Adapter.FilterListAdapter;
import com.example.doantest.Interface.ClickItemFilterListener;
import com.example.doantest.Model.DraftImageModel;
import com.example.doantest.Model.FilterModel;
import com.example.doantest.filter.PhotoLabFilter;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.SubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;

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
    Mat matOrigin, matNow, matSelect, matMark, mat1c;
    ArrayList<Mat>  mats;
    int now, intBrightness=0;
    String link;
    int size = 5, matrixSize = 3, sttDialog = 0;
    int posFilter;

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
    public Mat getMat(Bitmap bitmap){
        Mat nMat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8U);
        Utils.bitmapToMat(bitmap, nMat);
        return nMat;
    }
    public Dialog diaLogBlur(Context context, Mat origin){
        Dialog dialog = new Dialog(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_bokeh);
        ImageButton btnSelect, btnRemove, btnAction, btnDone, btnCancel;
        Button btnActionDone = dialog.findViewById(R.id.btnActionDone);
        LinearLayout layoutSize = dialog.findViewById(R.id.controlview);
        LinearLayout layoutactionView = dialog.findViewById(R.id.actionView);
        btnSelect = dialog.findViewById(R.id.btnSelect);
        btnRemove = dialog.findViewById(R.id.btnRemoveSelect);
        btnAction = dialog.findViewById(R.id.btnAction);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        SeekBar seekBarSize = dialog.findViewById(R.id.seekbarSize);
        SeekBar seekBarMatrixSize = dialog.findViewById(R.id.matrixSize);
        TextView tvValue = dialog.findViewById(R.id.tvValue);
        TextView tvMatrixSize = dialog.findViewById(R.id.tvMatrixSize);
        btnDone = dialog.findViewById(R.id.btnDone);
        SubsamplingScaleImageView scaleImageView = dialog.findViewById(R.id.imgview);

        matrixSize = 3;

        Mat mOrigin = new Mat(origin.height(), origin.width(), CvType.CV_8UC4);
        origin.copyTo(mOrigin);
        Mat newMat = new Mat(origin.height(), origin.width(), CvType.CV_8UC4);
        origin.copyTo(newMat);
        Bitmap bitmap = Bitmap.createBitmap(origin.cols(), origin.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(origin, bitmap);
        matMark = new Mat(origin.height(), origin.width(), CvType.CV_8UC4);
        mat1c = new Mat(origin.height(), origin.width(), CvType.CV_8UC1);
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
//                        scaleImageView.setImage(ImageSource.bitmap(bitmapReview));
                        setImage(bitmapReview, scaleImageView);
                    }
                }

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
        seekBarMatrixSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvMatrixSize.setText(seekBarMatrixSize.getProgress()+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                matrixSize = seekBarMatrixSize.getProgress();

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
                    }
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        Log.e("aaa", "dang xu ly");
                        for(int i = 1; i<maxY-1; i++){
                            for(int j = 1; j<maxX-1; j++){

                                double[] px = mat1c.get(i, j);
                                if(px[0] > 100){
                                    PhotoLabPixel[][] plP = getMatrixMat(origin, i, j, matrixSize);
                                    newMat1.put(i, j, matrix.MedianBlur(plP));
//                                            Log.e("pixel", origin.get(i,j)[1]+","+newMat1.get(i,j)[1]);
                                }
                                else{
                                    newMat1.put(i, j, origin.get(i, j));
                                }

                            }
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        Utils.matToBitmap(newMat1, newBitmap);
                        Bitmap abc = Bitmap.createBitmap(newMat1.width(), newMat1.height(), Bitmap.Config.RGB_565);
                        Utils.matToBitmap(newMat1, abc);
                        setImage(abc, scaleImageView);
                        Log.e("aaa", "xong ne");
                    }

                    @Override
                    protected void onProgressUpdate(Object[] values) {
                        super.onProgressUpdate(values);
                    }
                };
                task.execute();
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
                layoutactionView.setVisibility(View.GONE);
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
                layoutactionView.setVisibility(View.GONE);

            }
        });
        btnActionDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        for(int i = 0; i<maxY; i++){
                            for(int j = 0; j<maxX; j++){

                                double[] px = mat1c.get(i, j);
                                if(px[0] > 100){
                                    PhotoLabPixel[][] plP = getMatrixMat(origin, i, j, matrixSize);
                                    newMat1.put(i, j, matrix.MedianBlur(plP));
//
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
                        setImage(abc, scaleImageView);
                        newMat1.copyTo(newMat);
                        matMark = new Mat(origin.height(), origin.width(), CvType.CV_8UC4);
                        mat1c = new Mat(origin.height(), origin.width(), CvType.CV_8UC1);
                    }
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        return null;
                    }
                };
                task.execute();
            }
        });
        btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (sttDialog == R.id.btnAction){
                            layoutactionView.setVisibility(View.GONE);
                            sttDialog = 0 ;
                            return;
                        }
                        sttDialog = R.id.btnAction;
                        layoutactionView.setVisibility(View.VISIBLE);
                        layoutSize.setVisibility(View.GONE);

                    }
                });
        btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        add(newMat);

                        dialog.dismiss();
                    }
                });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        return dialog;
    }
    void setImage(Bitmap mBitmap, SubsamplingScaleImageView scaleImageView){
        PointF pointF = scaleImageView.getCenter();
        float scale = scaleImageView.getScale();
        scaleImageView.setImage(ImageSource.bitmap(mBitmap));
        scaleImageView.setScaleAndCenter(scale, pointF);
    }

    // getMatrixMat(origin, i, j, 1);
    PhotoLabPixel[][] getMatrixMat(Mat oMat, int i, int j, int k){
        PhotoLabPixel[][] arr = new PhotoLabPixel[2*k+1][2*k+1];
        int m = i - k, n = j - k;
        for(int x = 0; x <= 2 * k; x++){
            for(int y = 0; y <= 2 * k; y++){
                if(m+x < 0 || m+x >= oMat.height() || n+y < 0 || n+y >= oMat.width()){
                    arr[x][y] = new PhotoLabPixel(oMat.get(i, j));
                }
                else arr[x][y] = new PhotoLabPixel(oMat.get(m+x, n+y));
            }
        }
        return arr;
    }
    public Dialog filterA(Context context){
        Dialog dialog = new Dialog(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_filter);
        ArrayList<FilterModel> list = new ArrayList<FilterModel>();

        posFilter = 0;

        ImageButton btnDone, btnCancel;
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnDone = dialog.findViewById(R.id.btnDone);
//        SubsamplingScaleImageView scaleImageView = dialog.findViewById(R.id.imgview);
        ImageView scaleImageView = dialog.findViewById(R.id.imgview);

        Bitmap originBitmap = getBitMap();
//        setImage(getBitMap(), scaleImageView);
        scaleImageView.setImageBitmap(getBitMap());
        PhotoLabFilterV2 photoLabFilter = new PhotoLabFilterV2();

        RecyclerView rcvListFilter = dialog.findViewById(R.id.rcvListFilter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        FilterListAdapter adapter = new FilterListAdapter(new ClickItemFilterListener() {
            @Override
            public void ClickItemFilter(Bitmap bitmap, int pos) {
                if(pos == posFilter) return;
//                setImage(bitmap, scaleImageView);
                scaleImageView.setImageBitmap(bitmap);
                posFilter = pos;
            }
        });
        rcvListFilter.setAdapter(adapter);
        rcvListFilter.setLayoutManager(layoutManager);
        Bitmap none = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.none);
//        set Data
        list.add(new FilterModel(getBitMap(), "NONE", 0));
        list.add(new FilterModel(getBitMap(), "StarLitFilter", 1));
        list.add(new FilterModel(getBitMap(), "BlueMessFilter", 2));
        list.add(new FilterModel(getBitMap(), "AweStruckVibeFilter", 3));
        list.add(new FilterModel(getBitMap(), "LimeStutterFilter", 4));
        list.add(new FilterModel(getBitMap(), "NightWhisperFilter", 5));
        list.add(new FilterModel(getBitMap(), "Darker", 6));
        list.add(new FilterModel(getBitMap(), "IncreaseContrast", 7));
        list.add(new FilterModel(getBitMap(), "Brighten", 8));
        list.add(new FilterModel(getBitMap(), "Fade", 9));
        list.add(new FilterModel(getBitMap(), "PTL1", 10));
        list.add(new FilterModel(getBitMap(), "PTL2", 11));
        adapter.setData(list);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mat nMat = matNow;
                Bitmap nBitMap;
                switch (posFilter){
                    case 1:
                        nBitMap = photoLabFilter.getStarLitFilter().processFilter(getBitMap());
                        break;
                    case 2:
                        nBitMap = photoLabFilter.getBlueMessFilter().processFilter(getBitMap());
                        break;
                    case 3:
                        nBitMap = photoLabFilter.getAweStruckVibeFilter().processFilter(getBitMap());
                        break;
                    case 4:
                        nBitMap = photoLabFilter.getLimeStutterFilter().processFilter(getBitMap());
                        break;
                    case 5:
                        nBitMap = photoLabFilter.getNightWhisperFilter().processFilter(getBitMap());
                        break;
                    case 6:
                        nBitMap = photoLabFilter.getDarker().processFilter(getBitMap());
                        break;
                    case 7:
                        nBitMap = photoLabFilter.getIncreaseContrast().processFilter(getBitMap());
                        break;
                    case 8:
                        nBitMap = photoLabFilter.getBrighten().processFilter(getBitMap());
                        break;
                    case 9:
                        nBitMap = photoLabFilter.getFade().processFilter(getBitMap());
                        break;
                    case 10:
                        nBitMap = photoLabFilter.getPTL1().processFilter(getBitMap());
                        break;
                    case 11:
                        nBitMap = photoLabFilter.getPTL2().processFilter(getBitMap());
                        break;
                    default: nBitMap = getBitMap();
                }
                Utils.bitmapToMat(nBitMap, nMat);
                add(nMat);
                dialog.dismiss();
            }
        });
        return  dialog;
    }

}
