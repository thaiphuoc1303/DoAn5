package com.example.doantest.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doantest.DetailActivity;
import com.example.doantest.Model.LikeModel;
import com.example.doantest.Model.ResultPostModel;
import com.example.doantest.PhotoLabDate;
import com.example.doantest.ProfileActivity;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RvPostAdapter extends  RecyclerView.Adapter<RvPostAdapter.PostAdapterHolder>{
    Context context;
    ArrayList<ResultPostModel> list;
    Activity activity;

    public RvPostAdapter(Activity activity){
        this.activity = activity;
    }
    public void setData(ArrayList<ResultPostModel>list){
        this.list = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public PostAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_post_layout, parent, false);
        context = parent.getContext();
        return new PostAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapterHolder holder, @SuppressLint("RecyclerView") int position) {
        PhotoLabDate ptDate = new PhotoLabDate();
        ResultPostModel item = list.get(position);
        holder.tvName.setText(item.getAuthor().getName());
        holder.tvTime.setText(ptDate.compare(item.getTime(), context));
        holder.tvStatus.setText(item.getContent());
        holder.tvCount.setText(item.getLikeCount()+context.getString(R.string.like)+item.getCommentCount()+context.getString(R.string.comments));
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference storageRef = storageReference.child(item.getPathPicture());
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(holder.imgview);

            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference likePostRef = mDatabase.child("like").child(item.getToken());
        Query query = likePostRef.orderByChild("uID").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0){
                    holder.btnLike.setVisibility(View.GONE);
                    holder.btnLiked.setVisibility(View.VISIBLE);
                }
                else {
                    holder.btnLiked.setVisibility(View.GONE);
                    holder.btnLike.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("item", item);
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_right_to_left_in,R.anim.slide_right_to_left_out);
            }
        });
        holder.imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("item", item);
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_right_to_left_in,R.anim.slide_right_to_left_out);
            }
        });
        holder.btnLike.setOnClickListener(new View.OnClickListener() {
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
                            list.set(position, item);
                            Map<String, Object> postValues = item.getPostModel().toMap();
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/public/" + item.getToken(), postValues);
                            mDatabase.updateChildren(childUpdates);
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        });
        holder.btnLiked.setOnClickListener(new View.OnClickListener() {
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
                            list.set(position, item);
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
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        });
        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("uID", item.getAuthor().getuID());
                intent.putExtra("uName", item.getAuthor().getName());
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_bottom_to_top_in,R.anim.slide_bottom_to_top_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list==null) return 0;
        return list.size();
    }

    public class PostAdapterHolder extends RecyclerView.ViewHolder{
        ImageButton btnLike, btnLiked, btnShare, btnComment;
        ImageView imgview;
        TextView tvName, tvStatus, tvTime, tvCount;
        public PostAdapterHolder(@NonNull View view) {
            super(view);
            btnComment =  view.findViewById(R.id.btnComment);
            btnLike = view.findViewById(R.id.btnLike);
            btnLiked = view.findViewById(R.id.btnLiked);
            btnShare = view.findViewById(R.id.btnShare);
            tvName = view.findViewById(R.id.tvName);
            tvTime = view.findViewById(R.id.tvTime);
            tvStatus = view.findViewById(R.id.tvStatus);
            imgview = view.findViewById(R.id.imgview);
            tvCount = view.findViewById(R.id.tvCount);
        }
    }
}
