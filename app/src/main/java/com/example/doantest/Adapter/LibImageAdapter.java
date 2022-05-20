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
import com.example.doantest.Interface.ClickItemListener;
import com.example.doantest.Model.ImageModel;
import com.example.doantest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class LibImageAdapter extends  RecyclerView.Adapter<LibImageAdapter.LibImageHolder>{
    private ArrayList<ImageModel> list;
    ClickItemListener clickItemListener;
    Context context;

    public LibImageAdapter() {
    }

    public  LibImageAdapter(ClickItemListener clickItemListener) {
        this.clickItemListener = clickItemListener;
    }
    public void setData(ArrayList<ImageModel>list){
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
        ImageModel item = list.get(position);
        if(item==null) return;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference ref = storageReference.child(list.get(position).getLink());
        holder.imgReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickItemListener.ClickItemImage(item);
            }
        });
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
