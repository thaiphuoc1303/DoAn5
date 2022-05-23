package com.example.doantest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.doantest.Adapter.FilterListAdapter;
import com.example.doantest.Adapter.FontAdapter;
import com.example.doantest.Interface.ClickItemListener;
import com.example.doantest.Model.FilterModel;
import com.example.doantest.Model.FontModel;
import com.example.doantest.Model.ImageModel;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class PhotoLab {
    Mat matOrigin, matNow,
            matText, matMark, mat1c;
    ArrayList<Mat>  mats;
    int now;
    String link;
    int size = 5, matrixSize = 3, sttDialog = 0;
    int posFilter, layoutTextControl, textSize, font;
    String strText;
    Scalar sTextColor;
    Point pointText;


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
    void replace(Mat mat){
        matNow = mat;
        mats.remove(now);
        mats.add(mat);
    }
    public Bitmap getBitMap(){
        Bitmap bitmap = Bitmap.createBitmap(matNow.cols(), matNow.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(matNow, bitmap);
        return bitmap;
    }
    public Bitmap getBitMap(Mat mMat){
        Bitmap bitmap = Bitmap.createBitmap(mMat.cols(), mMat.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(mMat, bitmap);
        return bitmap;
    }
    public Mat getMat(Bitmap bitmap){
        Mat nMat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8U);
        Utils.bitmapToMat(bitmap, nMat);
        return nMat;
    }
    public Mat addText(Mat tMat, String text, Point point, int intFont, double scale, Scalar color, int thickness){

        if (text.trim().length() ==0){
            return tMat;
        }
        Imgproc.putText(tMat, text, point, intFont, scale, color, thickness);
        return tMat;
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
        Scalar scalar1 = new Scalar(255);
        scaleImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getActionMasked() == MotionEvent.ACTION_MOVE){
                    int intX = origin.width();
                    int intY = origin.height();
                    if (scaleImageView.isReady()) {
                        PointF pointF = scaleImageView.viewToSourceCoord(motionEvent.getX(), motionEvent.getY());
                        if(pointF.x<0 || pointF.x > intX || pointF.y < 0 || pointF.y > intY){
                            return false;
                        }
                        if (sttDialog == R.id.btnSelect){
                            Imgproc.circle(matMark, new Point(pointF.x, pointF.y), size, scalar, 2*size);
                            Imgproc.circle(mat1c, new Point(pointF.x, pointF.y), size, scalar1, 2*size);
                            Mat matReview = new Mat(origin.height(), origin.width(), CvType.CV_8UC4);
                            origin.copyTo(matReview);
                            Core.addWeighted( matReview, 1, matMark, 1, 1, matReview);
                            Bitmap bitmapReview = Bitmap.createBitmap(intX, intY, Bitmap.Config.RGB_565);
                            Utils.matToBitmap(matReview, bitmapReview);
                            setImage(bitmapReview, scaleImageView);
                        }
                    }

                    return true;
                }
                return false;

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
                        for(int i = 1; i<maxY-1; i++){
                            for(int j = 1; j<maxX-1; j++){

                                double[] px = mat1c.get(i, j);
                                if(px[0] > 100){
                                    PhotoLabPixel[][] plP = getMatrixMat(origin, i, j, matrixSize);
                                    newMat1.put(i, j, matrix.MedianBlur(plP));
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
                        newMat1.copyTo(newMat);
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
                int intX = origin.width();
                int intY = origin.height();
                matMark = new Mat(origin.height(), origin.width(), CvType.CV_8UC4);
                mat1c = new Mat(origin.height(), origin.width(), CvType.CV_8UC1);
                Mat matReview = new Mat(origin.height(), origin.width(), CvType.CV_8UC4);
                origin.copyTo(matReview);

                Bitmap bitmapReview = Bitmap.createBitmap(intX, intY, Bitmap.Config.RGB_565);
                Utils.matToBitmap(matReview, bitmapReview);
                setImage(bitmapReview, scaleImageView);
            }
        });
        btnActionDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int maxX = matOrigin.width();
//                int maxY = matOrigin.height();
//                int channel = mat1c.channels();
//                PhotoLabMatrix matrix = new PhotoLabMatrix();
//                Mat newMat1 = new Mat(maxY, maxX, CvType.CV_8UC4);
//                Bitmap newBitmap = Bitmap.createBitmap(newMat1.width(), newMat1.height(), Bitmap.Config.RGB_565);
//                AsyncTask task = new AsyncTask() {
//
//                    @Override
//                    protected void onPreExecute() {
//                        super.onPreExecute();
//                        Log.e("async", "1");
//                    }
//
//                    @Override
//                    protected Object doInBackground(Object[] objects) {
//                        for(int i = 0; i<maxY; i++){
//                            for(int j = 0; j<maxX; j++){
//
//                                double[] px = mat1c.get(i, j);
//                                if(px[0] > 100){
//                                    PhotoLabPixel[][] plP = getMatrixMat(origin, i, j, matrixSize);
//                                    newMat1.put(i, j, matrix.MedianBlur(plP));
////
//                                }
//                                else{
//                                    newMat1.put(i, j, origin.get(i, j));
//                                }
//
//                            }
//                        }
//                        Log.e("async", "2");
//                        return null;
//                    }
//
//                    @Override
//                    protected void onProgressUpdate(Object[] values) {
//                        super.onProgressUpdate(values);
//                        Log.e("async", "3");
//                    }
//
//                    @Override
//                    protected void onPostExecute(Object o) {
//                        super.onPostExecute(o);
//                        Utils.matToBitmap(newMat1, newBitmap);
//                        Bitmap abc = Bitmap.createBitmap(newMat1.width(), newMat1.height(), Bitmap.Config.RGB_565);
//                        Utils.matToBitmap(newMat1, abc);
//                        setImage(abc, scaleImageView);
//                        newMat1.copyTo(newMat);
//                        Log.e("async", "finish");
////                        matMark = new Mat(origin.height(), origin.width(), CvType.CV_8UC4);
////                        mat1c = new Mat(origin.height(), origin.width(), CvType.CV_8UC1);
//                    }
//                };
//                task.execute();
                matMark = new Mat(origin.height(), origin.width(), CvType.CV_8UC4);
                mat1c = new Mat(origin.height(), origin.width(), CvType.CV_8UC1);
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
    public Dialog addFilter(Context context){
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
        FilterListAdapter adapter = new FilterListAdapter(new ClickItemListener() {
            @Override
            public void ClickItemFilter(Bitmap bitmap, int pos) {
                if(pos == posFilter) return;
//                setImage(bitmap, scaleImageView);
                scaleImageView.setImageBitmap(bitmap);
                posFilter = pos;
            }

            @Override
            public void ClickItemImage(ImageModel imageModel) {

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
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        return  dialog;
    }

    public Dialog textDiaLog(Context context, Activity activity){
        Dialog dialog = new Dialog(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_text);
        SubsamplingScaleImageView imgview = (SubsamplingScaleImageView) dialog.findViewById(R.id.imgview);

        matText = new Mat(matNow.height(), matNow.width(), CvType.CV_8UC4);
        ImageButton btnCancel, btnDone, btnTextSize, btnTextColor,
                btnTextFont, btnAddText, btnColorPicker, btnConfirm;
        Button btnClear, btnTextDone;
        EditText edtText = dialog.findViewById(R.id.edtText);
        LinearLayout colorReview, layoutAddText, layoutTextFont, layoutTextColor, layoutTextSize;
        SeekBar sbTextSize = dialog.findViewById(R.id.sbTextSize);
        Spinner spFont = dialog.findViewById(R.id.spFont);

        btnConfirm = dialog.findViewById(R.id.btnConfirm);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnDone = dialog.findViewById(R.id.btnDone);
        btnTextSize = dialog.findViewById(R.id.btnTextSize);
        btnTextColor = dialog.findViewById(R.id.btnTextColor);
        btnTextFont = dialog.findViewById(R.id.btnTextFont);
        btnAddText = dialog.findViewById(R.id.btnAddText);
        btnColorPicker = dialog.findViewById(R.id.btnColorPicker);
        btnClear = dialog.findViewById(R.id.btnClear);
        btnTextDone = dialog.findViewById(R.id.btnTextDone);
        colorReview = dialog.findViewById(R.id.colorReview);
        layoutAddText = dialog.findViewById(R.id.layoutAddText);
        layoutTextFont = dialog.findViewById(R.id.layoutTextFont);
        layoutTextColor = dialog.findViewById(R.id.layoutTextColor);
        layoutTextSize = dialog.findViewById(R.id.layoutTextSize);

        pointText = new Point(20, 100);
        layoutTextControl = 0;
        sTextColor = new Scalar( 0, 0, 0, 255);
        strText = "";
        font = R.font.aparo;
        textSize = sbTextSize.getProgress();
        colorReview.setBackgroundColor(Color.argb(255, 0, 0, 0));

        layoutAddText.setVisibility(View.GONE);
        layoutTextColor.setVisibility(View.GONE);
        layoutTextFont.setVisibility(View.GONE);
        layoutTextSize.setVisibility(View.GONE);

        ArrayList<FontModel> listFont = new ArrayList<FontModel>();
        listFont.add(new FontModel(R.font.aparo, "Aparo"));
        listFont.add(new FontModel(R.font.svn_fiolex_girls, "Fiolex Girls"));
        listFont.add(new FontModel(R.font.svn_hole_hearted, "Hole Hearted"));
        listFont.add(new FontModel(R.font.utm_aristote, "Aristote"));
        listFont.add(new FontModel(R.font.ablation_bold, "Ablation Bold"));

        FontAdapter adapter = new FontAdapter(context, listFont);
        spFont.setAdapter(adapter);

        setImage(getBitMap(), imgview);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layoutTextControl == R.id.layoutAddText){
                    layoutAddText.setVisibility(View.GONE);
                    layoutTextControl =0;
                }
                else {
                    if (layoutTextControl != 0 )
                    dialog.findViewById(layoutTextControl).setVisibility(View.GONE);
                    layoutAddText.setVisibility(View.VISIBLE);
                    layoutTextControl = R.id.layoutAddText;
                }
            }
        });

        btnTextFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layoutTextControl == R.id.layoutTextFont){
                    layoutTextFont.setVisibility(View.GONE);
                    layoutTextControl =0;
                }
                else {
                    if (layoutTextControl != 0 )
                        dialog.findViewById(layoutTextControl).setVisibility(View.GONE);
                    layoutTextFont.setVisibility(View.VISIBLE);
                    layoutTextControl = R.id.layoutTextFont;
                }
            }
        });

        btnTextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layoutTextControl == R.id.layoutTextColor){
                    layoutTextColor.setVisibility(View.GONE);
                    layoutTextControl =0;
                }
                else {
                    if (layoutTextControl != 0 )
                        dialog.findViewById(layoutTextControl).setVisibility(View.GONE);
                    layoutTextColor.setVisibility(View.VISIBLE);
                    layoutTextControl = R.id.layoutTextColor;
                }
            }
        });

        btnTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layoutTextControl == R.id.layoutTextSize){
                    layoutTextSize.setVisibility(View.GONE);
                    layoutTextControl =0;
                }
                else {
                    if (layoutTextControl != 0 )
                        dialog.findViewById(layoutTextControl).setVisibility(View.GONE);
                    layoutTextSize.setVisibility(View.VISIBLE);
                    layoutTextControl = R.id.layoutTextSize;
                }
            }
        });

        btnColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog.Builder(context)
                        .setTitle("ColorPicker Dialog")
                        .setPreferenceName("MyColorPickerDialog")
                        .setPositiveButton( context.getString(R.string.confirm),
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
//                                        setLayoutColor(envelope);
                                        int[] agrb = envelope.getArgb();
                                        colorReview.setBackgroundColor(Color.argb(agrb[0], agrb[1], agrb[2], agrb[3]));
                                        sTextColor = new Scalar(agrb[1], agrb[2], agrb[3], agrb[0]);
                                        matText = addText(matNow.clone(), strText, pointText, font, textSize, sTextColor, textSize);
                                        setImage(getBitMap(matText), imgview);
                                    }
                                })
                        .setNegativeButton(context.getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                        .attachAlphaSlideBar(true) // the default value is true.
                        .attachBrightnessSlideBar(true)  // the default value is true.
                        .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                        .show();
            }
        });

        btnTextDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strText = edtText.getText().toString();
                matText = addText(matNow.clone(), strText, pointText, font, textSize, sTextColor, textSize);
                setImage(getBitMap(matText), imgview);
            }
        });
        spFont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                font = listFont.get(i).getId();
                matText = addText(matNow.clone(), strText, pointText, font, textSize, sTextColor, textSize);
                setImage(getBitMap(matText), imgview);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sbTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textSize = seekBar.getProgress();
                matText = addText(matNow.clone(), strText, pointText, font, textSize, sTextColor, textSize);
                setImage(getBitMap(matText), imgview);
            }
        });

        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                PointF sCoord = imgview.viewToSourceCoord(e.getX(), e.getY());
                pointText = new Point((int)sCoord.x, (int) sCoord.y);
                matText = addText(matNow.clone(), strText, pointText, font, textSize, sTextColor, textSize);
                setImage(getBitMap(matText), imgview);
                return true;
            }

        });
        imgview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add(matText);
                dialog.dismiss();
            }
        });
         btnConfirm.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 edtText.setText("");
                 add(matText);
             }
         });
         btnClear.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 edtText.setText("");
             }
         });
        return dialog;
    }
    public Dialog dialogSharpen(Context context){
        Dialog dialog = new Dialog(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_sharpen);
        LinearLayout controlview = dialog.findViewById(R.id.controlview);
        SeekBar seekbarSize =  dialog.findViewById(R.id.seekbarSize);

        SubsamplingScaleImageView scaleImageView = dialog.findViewById(R.id.imgview);
        scaleImageView.setImage(ImageSource.bitmap(getBitMap()));

        ImageButton btnCancel, btnDone, btnSelect, btnRemoveSelect, btnSharpen;
        TextView textTitle = dialog.findViewById(R.id.textTitle);
        TextView tvValue = dialog.findViewById(R.id.tvValue);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnDone = dialog.findViewById(R.id.btnDone);
        btnSelect = dialog.findViewById(R.id.btnSelect);
        btnRemoveSelect = dialog.findViewById(R.id.btnRemoveSelect);
        btnSharpen = dialog.findViewById(R.id.btnSharpen);

        Mat matOrigin = new Mat(matNow.height(), matNow.width(), CvType.CV_8UC4);
        Mat matnew = new Mat(matNow.height(), matNow.width(), CvType.CV_8UC4);
        matNow.copyTo(matnew);
        matNow.copyTo(matOrigin);
        matMark = new Mat(matOrigin.height(), matOrigin.width(), CvType.CV_8UC4);
        mat1c = new Mat(matOrigin.height(), matOrigin.width(), CvType.CV_8UC1);
        Scalar scalar = new Scalar(255, 0,0,255);
        Scalar scalar1 = new Scalar(255);

        sttDialog = 0;

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sttDialog == R.id.btnSelect){
                    controlview.setVisibility(View.GONE);
                    sttDialog = 0 ;
                    textTitle.setText(context.getString(R.string.sharpen));
                    return;
                }
                sttDialog = R.id.btnSelect;
                controlview.setVisibility(View.VISIBLE);
                textTitle.setText(context.getString(R.string.create_selection));
            }
        });

        btnSharpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int maxX = matOrigin.width();
                int maxY = matOrigin.height();
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
                        for(int i = 1; i<maxY-1; i++){
                            for(int j = 1; j<maxX-1; j++){

                                double[] px = mat1c.get(i, j);
                                if(px[0] > 100){
                                    PhotoLabPixel[][] plP = getMatrixMat(matOrigin, i, j, 1);
                                    newMat1.put(i, j, matrix.Sharpen(plP));
                                }
                                else{
                                    newMat1.put(i, j, matOrigin.get(i, j));
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
                        newMat1.copyTo(matnew);
                    }

                    @Override
                    protected void onProgressUpdate(Object[] values) {
                        super.onProgressUpdate(values);
                    }
                };
                task.execute();
            }
        });
        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

        });
        scaleImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getActionMasked() == MotionEvent.ACTION_MOVE && sttDialog == R.id.btnSelect){
                    int intX = matOrigin.width();
                    int intY = matOrigin.height();
                    if (scaleImageView.isReady()) {
                        PointF pointF = scaleImageView.viewToSourceCoord(motionEvent.getX(), motionEvent.getY());
                        if(pointF.x<0 || pointF.x > intX || pointF.y < 0 || pointF.y > intY){
                            return false;
                        }
                        if (sttDialog == R.id.btnSelect){
                            Imgproc.circle(matMark, new Point(pointF.x, pointF.y), size, scalar, 2*size);
                            Imgproc.circle(mat1c, new Point(pointF.x, pointF.y), size, scalar1, 2*size);
                            Mat matReview = new Mat(matOrigin.height(), matOrigin.width(), CvType.CV_8UC4);
                            matOrigin.copyTo(matReview);
                            Core.addWeighted( matReview, 1, matMark, 1, 1, matReview);
                            Bitmap bitmapReview = Bitmap.createBitmap(intX, intY, Bitmap.Config.RGB_565);
                            Utils.matToBitmap(matReview, bitmapReview);
                            setImage(bitmapReview, scaleImageView);
                        }
                    }

                    return true;
                }
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
        btnRemoveSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int intX = matOrigin.width();
                int intY = matOrigin.height();
                matMark = new Mat(intY, intX, CvType.CV_8UC4);
                mat1c = new Mat(intY, intX, CvType.CV_8UC1);
                Mat matReview = new Mat(intY, intX, CvType.CV_8UC4);
                matOrigin.copyTo(matReview);

                Bitmap bitmapReview = Bitmap.createBitmap(intX, intY, Bitmap.Config.RGB_565);
                Utils.matToBitmap(matReview, bitmapReview);
                setImage(bitmapReview, scaleImageView);
            }
        });
        seekbarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add(matnew);
                dialog.dismiss();
            }
        });
        return dialog;
    }
}
