package com.example.doantest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doantest.Adapter.DraftImageAdapter;
import com.example.doantest.Adapter.LibImageAdapter;
import com.example.doantest.Interface.ClickItemListener;
import com.example.doantest.Model.ImageModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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
        imageView = view.findViewById(R.id.imageView);
        layout1 = view.findViewById(R.id.layout1);
        layout2 = view.findViewById(R.id.layout2);
        layout3 = view.findViewById(R.id.layout3);
        btnEdit = view.findViewById(R.id.imgbtnEdit);
        btnRemove = view.findViewById(R.id.imgbtnRemove);
        layout2.setVisibility(View.GONE);
        draftView = view.findViewById(R.id.draftView);
        gridView =view.findViewById(R.id.libView);
        listDraft = new ArrayList<ImageModel>();
        listLib = new ArrayList<ImageModel>();

        LinearLayoutManager managerLayout = new LinearLayoutManager(container.getContext(), RecyclerView.HORIZONTAL, false);
        DraftImageAdapter draftImageAdapter = new DraftImageAdapter(new ClickItemListener() {
            @Override
            public void ClickItemFilter(Bitmap bitmap, int pos) {

            }

            @Override
            public void ClickItemImage(ImageModel imageModel) {
                draftClick(getContext(), imageModel);
            }
        });
        draftView.setLayoutManager(managerLayout);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(container.getContext(), 3);
        LibImageAdapter libImageAdapter = new LibImageAdapter(new ClickItemListener() {
            @Override
            public void ClickItemFilter(Bitmap bitmap, int pos) {

            }

            @Override
            public void ClickItemImage(ImageModel imageModel) {
                opendialog(getContext(),imageModel);
            }
        });
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

                        String link = Environment.getExternalStorageDirectory() + File.separator + "Documents/PhotoLab/";

                        file = new File(link);
                        if(!file.exists()){
                            file.mkdirs();
                        }
                        link +="draft.ptl";
                        file = new File(link);

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
                        this.link = file.getPath();
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
    void draftClick(Context context, ImageModel imageModel){
        StorageReference storageRef = storage.getReference();
        StorageReference storageItem = storageRef.child(imageModel.getLink());
        final long ONE_MEGABYTE = 1024 * 1024;
        storageItem.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                String link = Environment.getExternalStorageDirectory() + File.separator + "Documents/PhotoLab/";

                File file = new File(link);
                if(!file.exists()){
                    file.mkdirs();
                }
                link +="draft.ptl";
                file = new File(link);
                file.delete();
                try {
                    file.createNewFile();
                    Bitmap saveBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , bos); // YOU can also save it in JPEG
                    byte[] bitmapdata = bos.toByteArray();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                    Intent intent = new Intent(getActivity(), EditActivity.class);
                    intent.putExtra("link", link);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
    void opendialog(Context context, ImageModel imageModel){
        Dialog dialog = new Dialog(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_open_file_cloud);

        ImageButton btnCancel, btnEdit;
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnEdit = dialog.findViewById(R.id.btnEdit);
        ImageView imageView = dialog.findViewById(R.id.image);
        Button btnDelete = dialog.findViewById(R.id.btnDelete);
        Button btnDownload = dialog.findViewById(R.id.btnDownload);

        StorageReference storageRef = storage.getReference();
        StorageReference storageItem = storageRef.child(imageModel.getLink());
        final long ONE_MEGABYTE = 1024 * 1024;

        storageItem.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageItem.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Data for "images/island.jpg" is returns, use this as needed
                        String link = Environment.getExternalStorageDirectory() + File.separator + "Documents/PhotoLab/";

                        File file = new File(link);
                        if(!file.exists()){
                            file.mkdirs();
                        }
                        link +="draft.ptl";
                        file = new File(link);
                        file.delete();
                        try {
                            file.createNewFile();
                            Bitmap saveBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//Convert bitmap to byte array
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();

                            saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , bos); // YOU can also save it in JPEG

                            byte[] bitmapdata = bos.toByteArray();
//write the bytes in file
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                            Intent intent = new Intent(getActivity(), EditActivity.class);
                            intent.putExtra("link", link);
                            dialog.dismiss();
                            startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

            }
        });
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File localFile = File.createTempFile("images", "jpg");
                    storageItem.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Data for "images/island.jpg" is returns, use this as needed
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            String newPic = Environment.getExternalStorageDirectory() + File.separator + "Pictures/";
                            File file = new File(newPic);
                            if(!file.exists()){
                                file.mkdirs();
                            }
                            Date date = new Date();
                            newPic +="PhotoLabCloud_"+date.getTime()+".jpeg";
                            file = new File(newPic);
                            try {
                                file.createNewFile();

                                //Convert bitmap to byte array
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , bos); // YOU can also save it in JPEG
                                byte[] bitmapdata = bos.toByteArray();

                                //write the bytes in file
                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(bitmapdata);
                                fos.flush();
                                fos.close();

                                // scan file
                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri contentUri = Uri.fromFile(file);
                                mediaScanIntent.setData(contentUri);
                                context.sendBroadcast(mediaScanIntent);

                                Toast.makeText(context, "Đã lưu thành công", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageItem.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT);
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                    }
                });
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
