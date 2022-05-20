package com.example.doantest;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doantest.Adapter.DraftImageAdapter;
import com.example.doantest.Adapter.LibImageAdapter;
import com.example.doantest.Model.ImageModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class LibraryFragment extends Fragment {
    FirebaseUser user;
    FirebaseStorage storage;
    ImageButton btnOpenCamera, btnChooseImage, btnRemove, btnEdit;
    ImageView imageView;
    LinearLayout layout1, layout2, layout3;
    LayoutInflater layoutInflater;
    Bitmap bitmapImage;
    RecyclerView draftView, gridView;
    ArrayList<ImageModel> listDraft, listLib;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLARY = 2;
    String link;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        btnOpenCamera = view.findViewById(R.id.imageButtonCamera);
        btnChooseImage = view.findViewById(R.id.btnimage);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        layout1 = (LinearLayout) view.findViewById(R.id.layout1);
        layout2 = (LinearLayout) view.findViewById(R.id.layout2);
        layout3 = (LinearLayout) view.findViewById(R.id.layout3);
        btnEdit = view.findViewById(R.id.imgbtnEdit);
        btnRemove = view.findViewById(R.id.imgbtnRemove);
        layout2.setVisibility(View.GONE);
        draftView = view.findViewById(R.id.draftView);
        gridView =view.findViewById(R.id.libView);
        listDraft = new ArrayList<ImageModel>();
        listLib = new ArrayList<ImageModel>();

        LinearLayoutManager managerLayout = new LinearLayoutManager(container.getContext(), RecyclerView.HORIZONTAL, false);
        DraftImageAdapter draftImageAdapter = new DraftImageAdapter();
        draftView.setLayoutManager(managerLayout);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(container.getContext(), 3);
        LibImageAdapter libImageAdapter = new LibImageAdapter();
        gridView.setLayoutManager(gridLayoutManager);


//        managerLayout.set
        user = FirebaseAuth.getInstance().getCurrentUser();
        String uID = user.getUid();
        storage = FirebaseStorage.getInstance(getString(R.string.storageReference));
        StorageReference storageRef = storage.getReference();
        StorageReference folder = storageRef.child("/draft/"+uID);
        StorageReference folderLib = storageRef.child("/cloud/"+ uID);

        folder.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    ImageModel model = new ImageModel(item.getName(), item.getPath());
                    listDraft.add(model);
                }
                draftImageAdapter.setData(listDraft);
                draftView.setAdapter(draftImageAdapter);
            }
        });

        folderLib.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    ImageModel model = new ImageModel(item.getName(), item.getPath());
                    listLib.add(model);
                }
                libImageAdapter.setData(listLib);
                gridView.setAdapter(libImageAdapter);
            }
        });
        layoutInflater = inflater;

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                imageView.setImageBitmap(null);
                layout3.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmapImage== null) return;
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra("link", link);
                startActivity(intent);
            }
        });

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
//                outPutfileUri = Uri.fromFile(file);
//                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
//                startActivityForResult(captureIntent, PICK_FROM_CAMERA);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, PICK_FROM_CAMERA);
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
        Bitmap bitmap=null;

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK && data!=null) {

                    bitmap = (Bitmap) data.getExtras().get("data");
                    File file = null;
                    try {
                        file = new File(Environment.getExternalStorageDirectory() + File.separator +"Pictures/image_photolab.jpeg");

                        file.delete();

                        file.createNewFile();

//Convert bitmap to byte array
                        ByteArrayOutputStream  bos = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , bos); // YOU can also save it in JPEG

                        byte[] bitmapdata = bos.toByteArray();
//write the bytes in file
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                        link = file.getPath();
                        Log.e("Link", link);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;

            case PICK_FROM_GALLARY:

                if (resultCode == Activity.RESULT_OK) {
                    //pick image from gallery
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    // Get the cursor
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodableString = cursor.getString(columnIndex);
                    link = imgDecodableString;
                    Log.e("Link", link);
                    cursor.close();
                    bitmap = BitmapFactory.decodeFile(imgDecodableString);

                    // convert bitmap to Mat
                    Mat image = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8U, new Scalar(4));
                    Utils.bitmapToMat(bitmap, image);

                    bitmapImage = bitmap;

                }
                break;
        }
        if(bitmap!=null){
            bitmapImage = bitmap;
            imageView.setImageBitmap(bitmap);
            layout2.setVisibility(View.VISIBLE);
            layout1.setVisibility(View.GONE);
            layout3.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }

}
