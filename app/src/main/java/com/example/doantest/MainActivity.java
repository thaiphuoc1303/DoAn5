package com.example.doantest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity {
    public String mainText;
    BottomNavigationView bottomNavigationView;
    Fragment fragment;
    HomeFragment homeFragment;
    LibraryFragment libraryFragment;
    NotificationFragment notificationFragment;
    UserFragment userFragment;
    static {
        System.loadLibrary("NativeImageProcessor");
        if(OpenCVLoader.initDebug()){
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        initUI();
        permission();
    }
    void permission(){
        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                ||ActivityCompat.checkSelfPermission
                (this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
        }

    }
    void initUI() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        homeFragment = new HomeFragment();
        libraryFragment = new LibraryFragment();
        notificationFragment = new NotificationFragment();
        userFragment = new UserFragment();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        fragment = homeFragment;
                        break;
                    case R.id.action_library:
                        fragment = libraryFragment;
                        break;
                    case R.id.action_notification:
                        fragment = notificationFragment;
                        break;
                    case R.id.action_user:
                        fragment = userFragment;
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, fragment).commit();

                return true;
            }
        });
    }
}