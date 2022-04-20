package com.example.doantest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class ListStatusAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    ArrayList<StatusModel> listStatus;
    ImageButton btnLike, btnComment, btnShare;
    ImageView img;
    TextView tvName, tvTime;
    PhotoLabDate fDate;

    public ListStatusAdapter(Context context, int layout, ArrayList<StatusModel> listStatus){
        this.context = context;
        this.layout = layout;
        this.listStatus = listStatus;
        fDate = new PhotoLabDate();
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
        btnLike = view.findViewById(R.id.btnLike);
        btnComment = view.findViewById(R.id.btnComment);
        btnShare = view.findViewById(R.id.btnShare);
        tvName = view.findViewById(R.id.tvName);
        tvTime = view.findViewById(R.id.tvTime);
        img = view.findViewById(R.id.imgview);

        tvName.setText(listStatus.get(i).getName());
        tvTime.setText(fDate.compare(listStatus.get(i).getTime(), context));
        img.setImageBitmap(listStatus.get(i).getImg());
        return view;
    }
}
