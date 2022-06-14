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
import com.example.doantest.Interface.ClickItemListener;
import com.example.doantest.Model.ImageModel;
import com.example.doantest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DraftImageAdapter extends  RecyclerView.Adapter<DraftImageAdapter.DraftImageHolder>{
    private ArrayList<ImageModel> list;
    Context context;
    ClickItemListener clickItemListener;
    public DraftImageAdapter(ClickItemListener clickItemListener){
        this.clickItemListener = clickItemListener;
    }
    public void setData(ArrayList<ImageModel>list){
        this.list = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public DraftImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_horizontal_layout, parent, false);
        context = parent.getContext();
        return new DraftImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DraftImageHolder holder, int position) {
        ImageModel item = list.get(position);
        if(item==null) return;
        holder.tvName.setText(item.getName());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference ref = storageReference.child(list.get(position).getLink());
//        Glide.with(context).load(ref).into(holder.imgReview);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(holder.imgReview);
            }
        });
        holder.imgReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickItemListener.ClickItemImage(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list!=null) return  list.size();
        return 0;
    }

    public class DraftImageHolder extends RecyclerView.ViewHolder{
        private ImageView imgReview;
        private TextView tvName;
        public DraftImageHolder(@NonNull View itemView) {
            super(itemView);
            imgReview = itemView.findViewById(R.id.imgReview);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
