package com.example.doantest;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doantest.Adapter.LibImageAdapter;
import com.example.doantest.Adapter.PostManagementAdapter;
import com.example.doantest.Interface.ClickItemListener;
import com.example.doantest.Model.ImageModel;
import com.example.doantest.Model.ResultPostModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;

public class UserFragment extends Fragment {
    ImageButton btnLogout, btnEditProfile;
    TextView tvName;
    ImageView imgAvatar;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseStorage storage;
    DatabaseReference mDatabase;
    RecyclerView postview;
    ArrayList<ResultPostModel> list;
    PostManagementAdapter adapter;
    Bitmap newAvatar;
    Context context;
    Uri avtUri;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        btnLogout = view.findViewById(R.id.btnLogout);
        imgAvatar =  view.findViewById(R.id.imgAvatar);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        postview = view.findViewById(R.id.postview);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        list = new ArrayList<ResultPostModel>();
        context = getContext();
        LinearLayoutManager managerLayout = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        postview.setLayoutManager(managerLayout);
        adapter = new PostManagementAdapter(getActivity());
        postview.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("public").orderByChild("author/uID").equalTo(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                        list.add(item);
                    }
                    Collections.sort(list);
                    adapter.setData(list);
                }
            }
        });

        tvName = view.findViewById(R.id.tvName);
        Glide.with(context).load(user.getPhotoUrl()).into(imgAvatar);
        if(user.getDisplayName()!=null){
            tvName.setText(user.getDisplayName());
        }
        else {
            tvName.setText("No Name");
        }
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                startActivity(intent);
                getActivity().finishAffinity();
            }
        });
        return view;
    }
    void editProfile(){
        Dialog dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_Dialog);
        dialog.setContentView(R.layout.dialog_update_profile);
        dialog.setTitle(getString(R.string.edit_profile));
        EditText edtName = dialog.findViewById(R.id.edtName);
        ImageButton   btnChooseFile;
        Button btnClose, btnSave;
        btnClose = dialog.findViewById(R.id.btnClose);
        btnChooseFile = dialog.findViewById(R.id.btnChooseImage);
        btnSave = dialog.findViewById(R.id.btnSave);

        edtName.setText(user.getDisplayName());

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialogChooseImage= chooseImage();
                dialogChooseImage.show();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = edtName.getText().toString().trim();
                storage = FirebaseStorage.getInstance(getString(R.string.storageReference));
                StorageReference storageRef = storage.getReference().child("/avatar/I1jC3ijO3uMt81JhenPTNE0PHlE3/anime-chibi-lol-ekko.jpg");
                avtUri = null;
                UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
//                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        builder.setPhotoUri(uri);
//                        Log.e("AAA", uri.toString());
//                    }
//                });
                if(!newName.equals("")) builder.setDisplayName(newName);
                UserProfileChangeRequest profileUpdates = builder.build();
                user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, getString(R.string.update_successfully), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });

            }
        });
        dialog.show();
    }
    Dialog chooseImage(){
        Dialog dialogChooseImage = new Dialog(context, android.R.style.Theme_Light);
        dialogChooseImage.setContentView(R.layout.dialog_choose_image);
        dialogChooseImage.setTitle(getString(R.string.choose_your_avatar));

        RecyclerView listImgView = dialogChooseImage.findViewById(R.id.listImage);
        LibImageAdapter libImageAdapter = new LibImageAdapter(new ClickItemListener() {
            @Override
            public void ClickItemFilter(Bitmap bitmap, int pos) {

            }

            @Override
            public void ClickItemImage(ImageModel imageModel) {

            }
        });
        ArrayList<ImageModel> listImage = new ArrayList<ImageModel>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        listImgView.setLayoutManager(gridLayoutManager);

        String uID = user.getUid();
        storage = FirebaseStorage.getInstance(getString(R.string.storageReference));
        StorageReference storageRef = storage.getReference();
        StorageReference folderCloud = storageRef.child("/cloud/"+ uID);

        folderCloud.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    ImageModel model = new ImageModel(item.getName(), item.getPath());
                    listImage.add(model);
                }
                libImageAdapter.setData(listImage);
                listImgView.setAdapter(libImageAdapter);
            }
        });



        return dialogChooseImage;
    }
}
