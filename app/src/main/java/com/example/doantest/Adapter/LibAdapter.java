package com.example.doantest.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doantest.Model.ImageModel;
import com.example.doantest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class LibAdapter extends RecyclerView.Adapter<LibAdapter.LibViewHolder> {
    Context context;
    ArrayList<ImageModel> list;

    public void setData(ArrayList<ImageModel> newlist){
        this.list = newlist;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LibViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_grid_layout, parent, false);
        context = parent.getContext();
        return new LibViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibViewHolder holder, int position) {
        ImageModel item = list.get(position);
        if(item!= null)return;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference ref = storageReference.child(item.getLink());
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(holder.imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list!=null) list.size();
        return 0;
    }

    public class LibViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public LibViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgReview);
        }
    }
}
