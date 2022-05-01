package com.example.doantest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PostAdapter extends BaseAdapter {
    Context context;
    int layout, iTvStt;
    ArrayList<StatusModel> listStatus;
    ImageButton btnLike, btnComment, btnShare;
    ImageView img;
    TextView tvName, tvTime, tvStatus, tvLike;
    PhotoLabDate ptDate;

    public  PostAdapter (Context context, int layout, ArrayList<StatusModel> listStatus){
        this.context = context;
        this.layout = layout;
        this.listStatus = listStatus;
        ptDate = new PhotoLabDate();
    }

    @Override
    public int getCount() {
        return listStatus.size();
    }

    @Override
    public Object getItem(int i) {
        return listStatus.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);
        btnComment = view.findViewById(R.id.btnComment);
        btnLike = view.findViewById(R.id.btnLike);
        btnShare = view.findViewById(R.id.btnShare);
        tvName = view.findViewById(R.id.tvName);
        tvTime = view.findViewById(R.id.tvTime);
        tvLike = view.findViewById(R.id.tvLike);
        tvStatus = view.findViewById(R.id.tvStatus);
        img = view.findViewById(R.id.imgview);

        tvName.setText(listStatus.get(i).getName());
        tvTime.setText(ptDate.compare(listStatus.get(i).getTime(), context));
        img.setImageBitmap(listStatus.get(i).getImg());

        iTvStt = 0;
//        if()


        return view;
    }
}
