package com.example.doantest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doantest.Adapter.CommentAdapter;
import com.example.doantest.Model.CommentModel;
import com.example.doantest.Model.LikeModel;
import com.example.doantest.Model.PostModel;
import com.example.doantest.Model.ResultPostModel;
import com.example.doantest.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    ImageButton btnLike, btnLiked, btnShare, btnComment, btnAddPicture, btnSend, btnMore, btnBack;
    RecyclerView viewComments;
    LinearLayout layoutMore;
    ArrayList<CommentModel> listComments;
    CommentAdapter adapter;
    ImageView imgview;
    EditText edtComment;
    TextView tvName, tvStatus, tvTime, tvCount, tvDownload, tvEdit;
    ResultPostModel item;
    final int PICK_GALLERY = 22;
    Dialog loadingDialog;
    String link;
    boolean progressBoolean = false;
    FirebaseUser user;
    final int ONE_MEGABYTE = 1024*1024;

    int sttMore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();
        item = (ResultPostModel) intent.getSerializableExtra("item");
        init();
    }
    void init(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        edtComment = findViewById(R.id.edtComment);
        btnBack = findViewById(R.id.btnBack);
        btnMore = findViewById(R.id.btnMore);
        btnComment =  findViewById(R.id.btnComment);
        btnLike = findViewById(R.id.btnLike);
        btnLiked = findViewById(R.id.btnLiked);
        btnShare = findViewById(R.id.btnShare);
        tvName = findViewById(R.id.tvName);
        tvTime = findViewById(R.id.tvTime);
        tvStatus = findViewById(R.id.tvStatus);
        imgview = findViewById(R.id.imgview);
        tvCount = findViewById(R.id.tvCount);
        btnAddPicture = findViewById(R.id.btnAddPicture);
        btnSend = findViewById(R.id.btnSend);
        tvDownload = findViewById(R.id.tvDownload);
        tvEdit = findViewById(R.id.tvEdit);
        layoutMore = findViewById(R.id.layoutMore);
        PhotoLabDate date = new PhotoLabDate();
        tvName.setText(item.getAuthor().getName());
        tvTime.setText(date.compare(item.getTime(), DetailActivity.this));
        tvStatus.setText(item.getContent());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        listComments = new ArrayList<CommentModel>();
        viewComments = findViewById(R.id.listComments);
        LinearLayoutManager managerLayout = new LinearLayoutManager(DetailActivity.this, RecyclerView.VERTICAL, false);
        viewComments.setLayoutManager(managerLayout);
        adapter = new CommentAdapter();
        viewComments.setAdapter(adapter);
        DatabaseReference commentsRef = mDatabase.child("comments").child(item.getToken());
        commentsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CommentModel comment = snapshot.getValue(CommentModel.class);
                comment.setToken(snapshot.getKey());
                listComments.add(comment);
                adapter.notifyItemInserted(listComments.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        commentsRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                listComments = new ArrayList<CommentModel>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    CommentModel comment = snapshot.getValue(CommentModel.class);
                    comment.setToken(snapshot.getKey()+"");

                    listComments.add(comment);
                }
                adapter.setData(listComments);
            }
        });

        link = "";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference storageRef = storageReference.child(item.getPathPicture());
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(DetailActivity.this).load(uri).into(imgview);
            }
        });
        tvCount.setText(item.getLikeCount()+getString(R.string.like)+item.getCommentCount()+getString(R.string.comments));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
                            Intent intent = new Intent(DetailActivity.this, EditActivity.class);
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
        });
        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File localFile = File.createTempFile("images", "jpg");
                    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
                            newPic +="PhotoLabDownload_"+date.getTime()+".jpeg";
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
                                DetailActivity.this.sendBroadcast(mediaScanIntent);

                                Toast.makeText(DetailActivity.this, "Đã lưu thành công", Toast.LENGTH_SHORT).show();
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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                String comment = edtComment.getText().toString().trim();
                if (comment.equals("") && link.trim().equals("")){
                    return;
                }
                else {

                    if (!link.equals("")){
                        Uri uri =  Uri.fromFile(new File(link));
                        String name = uri.getLastPathSegment();
                        String ext = name.substring(name.lastIndexOf("."));
                        long time = date.getTime();
                        StorageReference uploadRef = storageReference.child("comments/PhotoLabComment"+time+ext);
                        StorageMetadata metadata = new StorageMetadata.Builder()
                                .setContentType("image/jpg")
                                .build();
                        UploadTask uploadTask = uploadRef.putFile(uri, metadata);
                        loadingDialog = new Dialog(DetailActivity.this);
                        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        loadingDialog.setContentView(R.layout.loading);
                        ImageView img = loadingDialog.findViewById(R.id.imgLoading);
                        Animation animation = AnimationUtils.loadAnimation(DetailActivity.this, R.anim.logo_loading);
                        img.setAnimation(animation);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                if(progressBoolean){
                                    loadingDialog.dismiss();
                                    progressBoolean = false;
                                }
                                Toast.makeText(DetailActivity.this, "Đăng bài không thành công, kiểm tra kết nối",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(DetailActivity.this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                                if(progressBoolean){
                                    CommentModel commentModel = new CommentModel(user.getUid(), user.getDisplayName(), comment, uploadRef.getPath(), date.getTime());
                                    item.setCommentCount(item.getCommentCount()+1);
                                    Map<String, Object> postValues = item.getPostModel().toMap();
                                    Map<String, Object> childUpdates = new HashMap<>();
                                    childUpdates.put("/public/" + item.getToken(), postValues);
                                    mDatabase.updateChildren(childUpdates);
                                    mDatabase.child("comments").child(item.getToken()).push().setValue(commentModel);
                                    loadingDialog.dismiss();
                                    progressBoolean = false;
                                }
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                if(!progressBoolean){
                                    loadingDialog.show();
                                    progressBoolean = true;
                                }
                            }
                        });;
                    }
                    else {
                        CommentModel commentModel = new CommentModel(user.getUid(), user.getDisplayName(), comment, "", date.getTime());
                        item.setCommentCount(item.getCommentCount()+1);
                        Map<String, Object> postValues = item.getPostModel().toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/public/" + item.getToken(), postValues);
                        mDatabase.updateChildren(childUpdates);
                        mDatabase.child("comments").child(item.getToken()).push().setValue(commentModel);
                    }
                }
                tvCount.setText(item.getLikeCount()+getString(R.string.like)+item.getCommentCount()+getString(R.string.comments));
                edtComment.setText("");
                link="";
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sttMore == 0){
                    sttMore = 1;
                    layoutMore.setVisibility(View.VISIBLE);
                }
                else {
                    sttMore = 0;
                    layoutMore.setVisibility(View.GONE);
                }
            }
        });
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtComment.setFocusable(true);
                edtComment.setFocusableInTouchMode(true);
                edtComment.requestFocus();

            }
        });
        btnAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, PICK_GALLERY);
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
                        tvCount.setText(item.getLikeCount()+getString(R.string.like)+item.getCommentCount()+getString(R.string.comments));
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
                            tvCount.setText(item.getLikeCount()+getString(R.string.like)+item.getCommentCount()+getString(R.string.comments));
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_GALLERY && resultCode == Activity.RESULT_OK){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            // Get the cursor
            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            link = imgDecodableString;

//            imgReview.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
//            imgReview.setVisibility(View.VISIBLE);

            cursor.close();
        }
    }
}