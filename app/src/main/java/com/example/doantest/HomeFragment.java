package com.example.doantest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {
    ListView listView;
    ArrayList<StatusModel> modelArrayList;
    ListStatusAdapter adapter;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        listView = view.findViewById(R.id.list);
        modelArrayList = new ArrayList<StatusModel>();
        Bitmap bitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.ekko);
        StatusModel model = new StatusModel("Phuoc", "img", "abc", new Date(), bitmap);
        modelArrayList.add(model);
        bitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.beemo);
        model = new StatusModel("Longg Hoangg", "img", "abc", new Date(), bitmap);
        modelArrayList.add(model);
        model = new StatusModel("Phuoc", "img", "abc", new Date(), bitmap);
        modelArrayList.add(model);
        adapter = new ListStatusAdapter(getContext(), R.layout.item_news_layout, modelArrayList);
        listView.setAdapter(adapter);
        return view;
    }
}
