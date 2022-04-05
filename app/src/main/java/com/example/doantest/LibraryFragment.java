package com.example.doantest;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LibraryFragment extends Fragment {
    Button btnOpenCamera, btnChooseImage, btnRemove, btnEdit;
    ImageView imageView;
    LinearLayout layout1, layout2;
    LayoutInflater layoutInflater;
    Bitmap image;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLARY = 2;

    Uri outPutfileUri;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        btnOpenCamera = (Button) view.findViewById(R.id.btnOpenCamera);
        btnChooseImage = (Button) view.findViewById(R.id.btnChooseImage);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        layout1 = (LinearLayout) view.findViewById(R.id.layout1);
        layout2 = (LinearLayout) view.findViewById(R.id.layout2);
        btnEdit = (Button) view.findViewById(R.id.btnEdit);
        btnRemove = (Button) view.findViewById(R.id.btnRemove);
        layout2.setVisibility(View.GONE);

        layoutInflater = inflater;

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                imageView.setImageBitmap(null);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), DemoTestActivity.class);
//                intent.putExtra("data", image);
                Mat originMat = new Mat();
//                Utils.bitmapToMat(image, originMat);
//                startActivity(intent);
            }
        });

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                outPutfileUri = Uri.fromFile(file);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
                startActivityForResult(captureIntent, PICK_FROM_CAMERA);
            }
        });
        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, PICK_FROM_GALLARY);
            }
        });
        return view;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    //pic coming from camera
                    Bitmap bitmap=null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), outPutfileUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;

            case PICK_FROM_GALLARY:

                if (resultCode == Activity.RESULT_OK) {
                    //pick image from gallery
                    Bitmap bitmap=null;
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    // Get the cursor
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    File file = new File(imgDecodableString);
                    bitmap = BitmapFactory.decodeFile(imgDecodableString);
                    ModelImage img = new ModelImage(bitmap);
                    image = bitmap;
                    if(bitmap!=null){
                        Intent intent = new Intent(getActivity(), DemoTestActivity.class);
                        intent.putExtra("img", img);
//                        startActivity(intent);
//                        getActivity().finish();
                        imageView.setImageBitmap(bitmap);
                        layout2.setVisibility(View.VISIBLE);
                        layout1.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }
}
