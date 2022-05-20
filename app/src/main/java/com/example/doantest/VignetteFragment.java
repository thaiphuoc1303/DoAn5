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
import com.zomato.photofilters.imageprocessors.subfilters.VignetteSubFilter;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class VignetteFragment extends Fragment {
    SeekBar sbVignette;
    TextView tvVignette;
    EditActivity editActivity;
    Mat matOrigin, matNow;

    public VignetteFragment(Mat mat){
        this.matOrigin = mat;
        matNow = matOrigin;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vignette, container, false);
        editActivity = (EditActivity) getActivity();
        init(view);

        return view;
    }
    void init(View view){
        sbVignette = view.findViewById(R.id.sbVignette);
        tvVignette = view.findViewById(R.id.tvVignette);
        sbVignette.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int n = seekBar.getProgress();
                tvVignette.setText(n+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                matNow = Mat.zeros(matOrigin.size(), matOrigin.type());
//                matOrigin.convertTo(matNow, -1, 1, seekBar.getProgress()-100);
                Filter myFilter = new Filter();
                VignetteSubFilter vg = new VignetteSubFilter(getContext(), seekBar.getProgress());
                myFilter.addSubFilter(vg);
                Bitmap outputImage = myFilter.processFilter(editActivity.mainPhoto.getBitMap(matOrigin));
                editActivity.setImageViewMain(outputImage);

                Utils.bitmapToMat(outputImage, matNow);
                editActivity.mainPhoto.replace(matNow);
            }
        });
    }
}
