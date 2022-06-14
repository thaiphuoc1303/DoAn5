package com.example.doantest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubFilter;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class SaturationFragment extends Fragment {
    SeekBar sbSaturation;
    TextView tvSaturation;
    EditActivity editActivity;
    Mat matOrigin, matNow;

    public SaturationFragment(Mat mat){
        this.matOrigin = mat;
        matNow = matOrigin;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saturation, container, false);
        editActivity = (EditActivity) getActivity();
        init(view);

        return view;
    }
    void init(View view){
        sbSaturation = view.findViewById(R.id.sbSaturation);
        tvSaturation = view.findViewById(R.id.tvSaturation);
        sbSaturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                double n = (double) (seekBar.getProgress())/100;
                tvSaturation.setText(n+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                matNow = Mat.zeros(matOrigin.size(), matOrigin.type());
                float value = (float) (seekBar.getProgress())/100;
                Filter myFilter = new Filter();
                SaturationSubFilter st = new SaturationSubFilter(value);
                myFilter.addSubFilter(st);
                Bitmap outputImage = myFilter.processFilter(editActivity.mainPhoto.getBitMap(matOrigin));
                editActivity.setImageViewMain(outputImage);
                Utils.bitmapToMat(outputImage, matNow);
                editActivity.mainPhoto.replace(matNow);
            }
        });
    }
}
