package com.example.doantest.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.doantest.Model.SpinnerIconModel;
import com.example.doantest.R;

import java.util.ArrayList;

public class SpinnerIconAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<SpinnerIconModel> list;
    ImageView icon;
    TextView text;

    public SpinnerIconAdapter(Context context, int layout, ArrayList<SpinnerIconModel> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;
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
        icon = view.findViewById(R.id.icon);
        text = view.findViewById(R.id.text);
        icon.setImageResource(list.get(i).getIcon());
        text.setText(list.get(i).getText());

        return view;
    }
}
