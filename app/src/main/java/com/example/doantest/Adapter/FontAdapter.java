package com.example.doantest.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.example.doantest.Model.FontModel;
import com.example.doantest.R;

import java.util.ArrayList;

public class FontAdapter extends BaseAdapter {
    Context context;
    ArrayList<FontModel> fontList;
    public FontAdapter(Context context, ArrayList<FontModel> fontList){
        this.fontList = fontList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return fontList != null ? fontList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return fontList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.font_item, viewGroup, false);
        TextView name = rootView.findViewById(R.id.name);
        name.setText(fontList.get(i).getName());
        name.setTypeface(ResourcesCompat.getFont(context, fontList.get(i).getId()));
        return rootView;
    }
}
