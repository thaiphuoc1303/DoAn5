package com.example.doantest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class SaveActivity extends AppCompatActivity {
    ImageButton btnSaveCloud, btnSaveLocal;
    ImageView imgReview;
    Bitmap mainBitmap;
    String link;
    Dialog dialog;
    boolean progressBoolean = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();
        link = intent.getStringExtra("link");
        mainBitmap = BitmapFactory.decodeFile(link);

        init();
        imgReview.setImageBitmap(mainBitmap);
    }
    void init(){
        btnSaveCloud = findViewById(R.id.btnSaveCloud);
        btnSaveLocal = findViewById(R.id.btnSaveLocal);
        imgReview = findViewById(R.id.imgReview);
        dialog = new Dialog(SaveActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loading);
        ImageView img = dialog.findViewById(R.id.imgLoading);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.logo_loading);
        img.setAnimation(animation);
        // event
        btnSaveLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPic = Environment.getExternalStorageDirectory() + File.separator + "Pictures/";
                File file = new File(newPic);
                if(!file.exists()){
                    file.mkdirs();
                }
                Date date = new Date();
                newPic +="PhotoLab_"+date.getTime()+".jpeg";
                file = new File(newPic);
                try {
                    file.createNewFile();

                    //Convert bitmap to byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    mainBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , bos); // YOU can also save it in JPEG
                    byte[] bitmapdata = bos.toByteArray();

                    //write the bytes in file
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                    btnSaveLocal.setEnabled(false);

                    // scan file
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(file);
                    mediaScanIntent.setData(contentUri);
                    SaveActivity.this.sendBroadcast(mediaScanIntent);

                    Toast.makeText(SaveActivity.this, "Đã lưu thành công", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btnSaveCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uID = user.getUid();
                FirebaseStorage storage = FirebaseStorage.getInstance("gs://doancoso5-test.appspot.com");
                StorageReference storageRef = storage.getReference();
                Date date = new Date();
                String fileName = "PhotoLab_"+date.getTime()+".jpg";
                StorageReference newFile = storageRef.child("/cloud/"+uID+"/"+fileName);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                mainBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , bos); // YOU can also save it in JPEG

                byte[] bitmapdata = bos.toByteArray();
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpg")
                        .build();

                UploadTask uploadTask = newFile.putBytes(bitmapdata, metadata);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        if(progressBoolean){
                            dialog.dismiss();
                            progressBoolean = false;
                        }
                        Toast.makeText(SaveActivity.this, "Tải lên Cloud không thành công, kiểm tra kết nối", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        Toast toast = Toast.makeText(SaveActivity.this, "Lưu ảnh thành công", Toast.LENGTH_SHORT);
                        toast.show();
                        if(progressBoolean){
                            dialog.dismiss();
                            progressBoolean = false;
                        }
                        btnSaveCloud.setEnabled(false);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        if(!progressBoolean){
                            dialog.show();
                            progressBoolean = true;
                        }
                    }
                });
            }
        });
    }
}
