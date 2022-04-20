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

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class ContrastFragment extends Fragment {
    SeekBar sbContrast;
    TextView tvContrast;
    EditActivity editActivity;
    Mat matOrigin, matNow;

    public ContrastFragment(Mat mat){
        this.matOrigin = mat;
        matNow = matOrigin;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contrast, container, false);
        editActivity = (EditActivity) getActivity();
        init(view);

        return view;
    }
    void init(View view){
        sbContrast = view.findViewById(R.id.seekBar);
        tvContrast = view.findViewById(R.id.tvContrast);
        sbContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                double n = (double) (seekBar.getProgress())/100;
                tvContrast.setText(n+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                matNow = Mat.zeros(matOrigin.size(), matOrigin.type());
                double value = (double) (seekBar.getProgress())/100;
                matOrigin.convertTo(matNow, -1, value, 0);
                Bitmap bitmap = Bitmap.createBitmap(matNow.cols(), matNow.rows(), Bitmap.Config.RGB_565);
                Utils.matToBitmap(matNow, bitmap);
                editActivity.mainPhoto.remove();
                editActivity.mainPhoto.mats.remove(editActivity.mainPhoto.now);
                editActivity.mainPhoto.now--;
                editActivity.mainPhoto.add(matNow);
                editActivity.setImageViewMain(bitmap);
            }
        });
    }
}
