package com.example.doantest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class DemoTestActivity extends AppCompatActivity {
    ImageButton btnCrop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_test);
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