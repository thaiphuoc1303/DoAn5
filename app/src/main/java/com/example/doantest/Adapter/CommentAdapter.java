package com.example.doantest.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doantest.Model.CommentModel;
import com.example.doantest.PhotoLabDate;
import com.example.doantest.ProfileActivity;
import com.example.doantest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CommentAdapter extends  RecyclerView.Adapter<CommentAdapter.CommentAdapterHolder>{
    Context context;
    ArrayList<CommentModel> list;
    Activity activity;

    public CommentAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setData(ArrayList<CommentModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_layout, parent, false);
        context = parent.getContext();
        return new CommentAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapterHolder holder, int position) {
        PhotoLabDate ptDate = new PhotoLabDate();
        CommentModel item = list.get(position);
        holder.tvTime.setText(ptDate.compare(item.getTime(), context));
        holder.tvName.setText(item.getName());
        holder.tvContent.setText(item.getContent());
        if (!item.getPathPicture().trim().equals("")){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference storageRef = storageReference.child(item.getPathPicture());
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(holder.img);
                }
            });
        }
        else {
            holder.img.setVisibility(View.GONE);
        }
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("uID", item.getuID());
                intent.putExtra("uName", item.getName());
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


    public class CommentAdapterHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvContent, tvTime;
        ImageView img;

        public CommentAdapterHolder(@NonNull View view) {
            super(view);
            tvContent = view.findViewById(R.id.tvContent);
            tvName = view.findViewById(R.id.tvName);
            tvTime = view.findViewById(R.id.tvTime);
            img = view.findViewById(R.id.img);
        }
    }
}
