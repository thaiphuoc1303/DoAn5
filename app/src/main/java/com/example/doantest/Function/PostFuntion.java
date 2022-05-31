package com.example.doantest.Function;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.doantest.Model.LikeModel;
import com.example.doantest.Model.ResultPostModel;
import com.example.doantest.PhotoLabDate;
import com.example.doantest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostFuntion {
    public static Dialog dialogPostDetail(Context context, ResultPostModel item){
        Dialog dialog = new Dialog(context, android.R.style.Theme_NoTitleBar);
        dialog.setContentView(R.layout.dialog_post_detail);
        ImageButton btnLike, btnLiked, btnShare, btnComment, btnAddPicture, btnSend;
        PhotoLabDate ptDate;
        ImageView imgview;
        EditText edtComment = dialog.findViewById(R.id.edtComment);
        TextView tvName, tvStatus, tvTime, tvCount;
        btnComment =  dialog.findViewById(R.id.btnComment);
        btnLike = dialog.findViewById(R.id.btnLike);
        btnLiked = dialog.findViewById(R.id.btnLiked);
        btnShare = dialog.findViewById(R.id.btnShare);
        tvName = dialog.findViewById(R.id.tvName);
        tvTime = dialog.findViewById(R.id.tvTime);
        tvStatus = dialog.findViewById(R.id.tvStatus);
        imgview = dialog.findViewById(R.id.imgview);
        tvCount = dialog.findViewById(R.id.tvCount);
        btnAddPicture = dialog.findViewById(R.id.btnAddPicture);
        btnSend = dialog.findViewById(R.id.btnSend);
        PhotoLabDate date = new PhotoLabDate();
        tvName.setText(item.getAuthor().getName());
        tvTime.setText(date.compare(item.getTime(), context));
        tvStatus.setText(item.getContent());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference storageRef = storageReference.child(item.getPathPicture());
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(imgview);
            }
        });
        tvCount.setText(item.getLikeCount()+context.getString(R.string.like)+item.getCommentCount()+context.getString(R.string.comments));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference likePostRef = mDatabase.child("like").child(item.getToken());
        Query query = likePostRef.orderByChild("uID").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0){
                    btnLike.setVisibility(View.GONE);
                    btnLiked.setVisibility(View.VISIBLE);
                }
                else {
                    btnLiked.setVisibility(View.GONE);
                    btnLike.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                // Start the Intent
//                startActivityForResult(galleryIntent, 123);
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot results = task.getResult();
                        if (results.getChildrenCount() == 0){
                            LikeModel likeItem = new LikeModel(new Date().getTime(), user.getUid(), user.getDisplayName());
                            mDatabase.child("like").child(item.getToken()).push().setValue(likeItem);

                            item.setLikeCount(item.getLikeCount()+1);
                            Map<String, Object> postValues = item.getPostModel().toMap();
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/public/" + item.getToken(), postValues);
                            mDatabase.updateChildren(childUpdates);
                        }
                        tvCount.setText(item.getLikeCount()+context.getString(R.string.like)+item.getCommentCount()+context.getString(R.string.comments));
                    }
                });
            }
        });
        btnLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot results = task.getResult();
                        if (results.getChildrenCount() == 0){

                        }
                        else{
                            item.setLikeCount(item.getLikeCount()-1);
                            Map<String, Object> postValues = item.getPostModel().toMap();
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/public/" + item.getToken(), postValues);
                            mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    for (DataSnapshot postSnapshot: results.getChildren()) {
                                        likePostRef.child(postSnapshot.getKey()).removeValue();
                                    }
                                }
                            });
                            tvCount.setText(item.getLikeCount()+context.getString(R.string.like)+item.getCommentCount()+context.getString(R.string.comments));
                        }
                    }
                });
            }
        });
        return dialog;
    }
}
