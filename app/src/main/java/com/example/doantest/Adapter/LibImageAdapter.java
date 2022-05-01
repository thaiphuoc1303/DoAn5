package com.example.doantest.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doantest.Model.DraftImageModel;
import com.example.doantest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class LibImageAdapter extends  RecyclerView.Adapter<LibImageAdapter.LibImageHolder>{
    private ArrayList<DraftImageModel> list;
    Context context;
    public void setData(ArrayList<DraftImageModel>list){
        this.list = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public LibImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_grid_layout, parent, false);
        context = parent.getContext();
        return new LibImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibImageHolder holder, int position) {
        DraftImageModel item = list.get(position);
        if(item==null) return;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference ref = storageReference.child(list.get(position).getLink());
//        Glide.with(context).load(ref).into(holder.imgReview);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(holder.imgReview);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list!=null) return  list.size();
        return 0;
    }

    public class LibImageHolder extends RecyclerView.ViewHolder{
        private ImageView imgReview;
        public LibImageHolder(@NonNull View itemView) {
            super(itemView);
            imgReview = itemView.findViewById(R.id.imgReview);
        }
    }
}
