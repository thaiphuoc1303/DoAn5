package com.example.doantest.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doantest.DetailActivity;
import com.example.doantest.Model.NotificationModel;
import com.example.doantest.Model.ResultPostModel;
import com.example.doantest.PhotoLabDate;
import com.example.doantest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NotificationAdapter extends  RecyclerView.Adapter<NotificationAdapter.NotificationAdapterHolder>{
    Context context;
    ArrayList<NotificationModel> list;

    public void setData(ArrayList<NotificationModel> list){
        this.list = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public NotificationAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_layout, parent, false);
        context = parent.getContext();
        return new NotificationAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapterHolder holder, int position) {
        NotificationModel item = list.get(position);
        PhotoLabDate dt = new PhotoLabDate();
        holder.tvContent.setText(item.getContent());
        holder.time.setText(dt.compare(item.getTime(), context));
        switch (item.getIcon()){
            case 1:
                holder.icon.setImageResource(R.drawable.heart_red);
                break;
            case 2:
                holder.icon.setImageResource(R.drawable.ic_comment_color);
        }
        holder.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("public").child(item.getToken());
                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        ResultPostModel item = dataSnapshot.getValue(ResultPostModel.class);
                        item.setToken(dataSnapshot.getKey());
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("item", item);
                        ContextCompat.startActivity(context, intent, null);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list==null) return 0;
        return list.size();
    }

    public class NotificationAdapterHolder extends RecyclerView.ViewHolder {
        TextView tvContent, time;
        ImageView icon;
        public NotificationAdapterHolder(@NonNull View view) {
            super(view);
            tvContent = view.findViewById(R.id.tvContent);
            time = view.findViewById(R.id.time);
            icon = view.findViewById(R.id.icon);
        }
    }
}
