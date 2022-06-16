package com.example.doantest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doantest.Adapter.NotificationAdapter;
import com.example.doantest.Model.LikeModel;
import com.example.doantest.Model.NotificationModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class NotificationFragment extends Fragment {
    RecyclerView notificationView;
    NotificationAdapter adapter;
    ArrayList<NotificationModel> list;
    FirebaseUser user;
    Context context;
    private DatabaseReference mDatabase;
    Date date;

    SharedPreferences sharedPreferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        date = new Date();
        context = getContext();
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        init(view);
        return view;
    }
    void init(View view){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        notificationView = view.findViewById(R.id.listNotification);
        adapter = new NotificationAdapter();
        list = new ArrayList<NotificationModel>();
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        notificationView.setAdapter(adapter);
        notificationView.setLayoutManager(manager);
        getData();
    }
    void getData(){
        DatabaseReference postRef = mDatabase.child("public");
        long old = sharedPreferences.getLong("lastcheck", date.getTime());

        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                postRef.orderByChild("author/uID").equalTo(user.getUid()).get()
                        .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getChildrenCount()==0) return;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    DatabaseReference likeRef = mDatabase.child("like").child(snapshot.getKey());
                                    likeRef
//                                            .orderByChild("time").endBefore(old)
                                            .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                                @Override
                                                public void onSuccess(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getChildrenCount() == 0) return;
                                                    long time = 0;
                                                    for (DataSnapshot likeSnapshot1 :dataSnapshot.getChildren()) {
                                                        time = likeSnapshot1.getValue(LikeModel.class).getTime();
                                                    }
                                                    NotificationModel item = new NotificationModel(snapshot.getKey(),
                                                            "Bài viết của bạn có "+ dataSnapshot.getChildrenCount() +" lượt thích mới.",
                                                            1, time);
                                                    list.add(item);
                                                    adapter.setData(list);
                                                }
                                            });
                                    DatabaseReference commentRef = mDatabase.child("comments").child(snapshot.getKey());
                                    commentRef
//                                            .orderByChild("time").startAt(old)
                                            .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                                @Override
                                                public void onSuccess(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getChildrenCount() == 0) return;
                                                    long time = 0;
                                                    for (DataSnapshot commentSnapshot :
                                                            dataSnapshot.getChildren()) {
                                                        time = commentSnapshot.getValue(LikeModel.class).getTime();
                                                    }
                                                    NotificationModel item = new NotificationModel(snapshot.getKey(),
                                                            "Bài viết của bạn có "+ dataSnapshot.getChildrenCount() +" lượt bình luận mới.",
                                                            2, time);
                                                    list.add(item);
                                                    Collections.sort(list);
                                                    adapter.setData(list);
                                                }
                                            });
                                }
                            }
                        });

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("lastcheck", date.getTime());
                editor.apply();

            }
        };
        asyncTask.execute();

    }
}
