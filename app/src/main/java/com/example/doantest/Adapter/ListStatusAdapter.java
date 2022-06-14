package com.example.doantest.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class ListStatusAdapter extends BaseAdapter {
    Context context;
    int layout;
    ImageButton btnLike, btnLiked, btnShare, btnComment;
    PhotoLabDate ptDate;
    ImageView imgview;
    TextView tvName, tvStatus, tvTime, tvCount;
    ArrayList<ResultPostModel> list;
    public ListStatusAdapter(Context context, int layout, ArrayList<ResultPostModel> list){
        this.context = context;
        this.list = list;
        this.layout = layout;
    }
    public void setData(ArrayList<ResultPostModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);
        btnComment =  view.findViewById(R.id.btnComment);
        btnLike = view.findViewById(R.id.btnLike);
        btnLiked = view.findViewById(R.id.btnLiked);
        btnShare = view.findViewById(R.id.btnShare);
        tvName = view.findViewById(R.id.tvName);
        tvTime = view.findViewById(R.id.tvTime);
        tvStatus = view.findViewById(R.id.tvStatus);
        imgview = view.findViewById(R.id.imgview);
        tvCount = view.findViewById(R.id.tvCount);
        ptDate = new PhotoLabDate();
        ResultPostModel item = list.get(i);
        tvName.setText(item.getAuthor().getName());
        tvTime.setText(ptDate.compare(item.getTime(), context));
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

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLiked.setVisibility(View.VISIBLE);
                btnLike.setVisibility(View.GONE);
//                evtLike(query, user, mDatabase, item, likePostRef);
            }
        });
        btnLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLike.setVisibility(View.VISIBLE);
                btnLiked.setVisibility(View.GONE);
//                evtLike(query, user, mDatabase, item, likePostRef);
            }
        });

        return view;
    }

}
