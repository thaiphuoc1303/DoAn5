package com.example.doantest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doantest.Adapter.ListStatusAdapter;
import com.example.doantest.Model.ResultPostModel;
import com.example.doantest.Adapter.RvPostAdapter;
import com.example.doantest.Adapter.SpinnerIconAdapter;
import com.example.doantest.Model.PostModel;
import com.example.doantest.Model.SpinnerIconModel;
import com.example.doantest.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {
    Button btnCreate;
    ArrayList<ResultPostModel> modelArrayList;
    FirebaseUser user;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    Uri selectedImage;
    String link;
    Dialog loadingDialog;
    ImageView imgReview;
    RecyclerView listPost;
    private DatabaseReference mDatabase;
    boolean progressBoolean = false;
    RvPostAdapter rvPostAdapter;
    final int PICK_FROM_GALLARY = 222;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);

        return view;
    }

    void init(View view){
        listPost = view.findViewById(R.id.listPost);
        btnCreate = view.findViewById(R.id.btnCreate);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCreate();

            }
        });
        LinearLayoutManager managerLayout = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        listPost.setLayoutManager(managerLayout);
        rvPostAdapter = new RvPostAdapter(getActivity());
        listPost.setAdapter(rvPostAdapter);

        modelArrayList = new ArrayList<ResultPostModel>();
        mDatabase.child("public").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    DataSnapshot results = task.getResult();
                    for (DataSnapshot postSnapshot: results.getChildren()) {
                        // TODO: handle the post
                        ResultPostModel item = postSnapshot.getValue(ResultPostModel.class);
                        item.setToken(postSnapshot.getKey());
                        Log.e("token", postSnapshot.getKey());
                        modelArrayList.add(item);
                    }
                    rvPostAdapter.setData(modelArrayList);
                }
            }
        });
    }

    void dialogCreate(){
        Dialog dialog = new Dialog(getContext(), R.style.Theme_DoAnTest);
        dialog.setContentView(R.layout.dialog_create_post);
        EditText edtDescription = dialog.findViewById(R.id.edtDescription);
        ImageButton btnAddImage = dialog.findViewById(R.id.btnAddImage);
        imgReview = dialog.findViewById(R.id.imgReview);
        Spinner spShare = dialog.findViewById(R.id.spShare);
        Button btnPost = dialog.findViewById(R.id.btnPost);
        ArrayList<SpinnerIconModel> list = new ArrayList<SpinnerIconModel>();
        SpinnerIconAdapter adapter = new SpinnerIconAdapter(getContext(), R.layout.icon_list, list);
        list.add(new SpinnerIconModel(R.drawable.ic_public_24, "public"));
        list.add(new SpinnerIconModel(R.drawable.ic_private, "private"));
        spShare.setAdapter(adapter);
        selectedImage =null;
        link = "";


        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, PICK_FROM_GALLARY);
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = edtDescription.getText().toString().trim();
                if (content.equals("")){
                    edtDescription.setError(getContext().getString(R.string.Field_cannot_be_left_blank));
                    edtDescription.setFocusable(true);
                    edtDescription.setFocusableInTouchMode(true);
                    edtDescription.requestFocus();
                    return;
                }
                if(!link.equals("")){
                    Uri uri =  Uri.fromFile(new File(link));
                    Date date = new Date();
                    String name = uri.getLastPathSegment();
                    String ext = name.substring(name.lastIndexOf("."));
                    long time = date.getTime();
                    StorageReference uploadRef = storageRef.child("public/PhotoLabPublic"+time+ext);
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType("image/jpg")
                            .build();
                    UploadTask uploadTask = uploadRef.putFile(uri, metadata);

                    loadingDialog = new Dialog(getContext());
                    loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    loadingDialog.setContentView(R.layout.loading);
                    ImageView img = loadingDialog.findViewById(R.id.imgLoading);
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.logo_loading);
                    img.setAnimation(animation);


                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            if(progressBoolean){
                                loadingDialog.dismiss();
                                progressBoolean = false;
                            }
                            Toast.makeText(getContext(), "Đăng bài không thành công, kiểm tra kết nối",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                            if(progressBoolean){
                                PostModel post = new PostModel(new UserModel(user.getUid(), user.getDisplayName()), content, uploadRef.getPath(), time, 0,0);
                                mDatabase.child("public").push().setValue(post);
                                loadingDialog.dismiss();
                                progressBoolean = false;
                                dialog.dismiss();
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
            }
        });

        dialog.show();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        modelArrayList = new ArrayList<ResultPostModel>();
//        mDatabase.child("public").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    DataSnapshot results = task.getResult();
//                    for (DataSnapshot postSnapshot: results.getChildren()) {
//                        // TODO: handle the post
//                        ResultPostModel item = postSnapshot.getValue(ResultPostModel.class);
//                        item.setToken(postSnapshot.getKey());
//                        Log.e("token", postSnapshot.getKey());
//                        modelArrayList.add(item);
//                    }
//                    rvPostAdapter.setData(modelArrayList);
//                }
//            }
//        });
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FROM_GALLARY && resultCode == Activity.RESULT_OK){
            selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            // Get the cursor
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            link = imgDecodableString;
            imgReview.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            imgReview.setVisibility(View.VISIBLE);

            cursor.close();
        }
    }
}
