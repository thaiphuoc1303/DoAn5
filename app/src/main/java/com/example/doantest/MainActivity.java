package com.example.doantest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        HomeFragment homeFragment = new HomeFragment();
        LibraryFragment libraryFragment = new LibraryFragment();
        NotificationFragment notificationFragment = new NotificationFragment();
        UserFragment userFragment = new UserFragment();

        initUI();
    }
    void initUI() {

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_library:
                        fragment = new LibraryFragment();
                        break;
                    case R.id.action_notification:
                        fragment = new NotificationFragment();
                        break;
                    case R.id.action_user:
                        fragment = new UserFragment();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, fragment).commit();

                return true;
            }
        });
    }
}