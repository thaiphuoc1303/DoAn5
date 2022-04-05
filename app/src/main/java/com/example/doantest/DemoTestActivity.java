package com.example.doantest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class DemoTestActivity extends AppCompatActivity {
    ImageButton btnCrop;
    ModelImage image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_test);
        Intent intent = getIntent();
//        diadiemId = (int) intent.getIntExtra("data", 0);
        image = (ModelImage) intent.getSerializableExtra("data");
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        init();
        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    void init(){
        btnCrop = (ImageButton) findViewById(R.id.btnCrop);
    }
}