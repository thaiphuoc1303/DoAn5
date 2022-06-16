package com.example.doantest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.doantest.Adapter.RvPostAdapter;
import com.example.doantest.Model.ResultPostModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class ProfileActivity extends AppCompatActivity {
    String uID, uName;
    DatabaseReference reference;
    ImageButton btnBack;
    TextView tvName;
    RecyclerView postview;
    ArrayList<ResultPostModel> list;
//    ImageView imgAvatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();
        if (intent == null) finish();
        uID = intent.getStringExtra("uID");
        uName = intent.getStringExtra("uName");
        if (uID == null) finish();
        init();
    }
    private void init(){
        btnBack = findViewById(R.id.btnBack);
        tvName = findViewById(R.id.tvName);
        tvName.setText(uName);
        postview = findViewById(R.id.postview);
        list = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        postview.setLayoutManager(manager);
        RvPostAdapter adapter = new RvPostAdapter(this);
        postview.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference().child("public");
        reference.orderByChild("author/uID").equalTo(uID).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()){
                    ResultPostModel item = itemSnapshot.getValue(ResultPostModel.class);
                    item.setToken(itemSnapshot.getKey());
                    list.add(item);
                }
                Collections.sort(list);
                adapter.setData(list);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_top_to_bottom_in,R.anim.slide_top_to_bottom_out);
    }
}