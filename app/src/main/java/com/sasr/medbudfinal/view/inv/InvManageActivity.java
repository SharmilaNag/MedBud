package com.sasr.medbudfinal.view.inv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.data.userinfo.UserInfoDao;
import com.sasr.medbudfinal.model.UserInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InvManageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_manage);
        Toolbar toolbar = findViewById(R.id.homeToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap recentIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_med_bud_round); // Initialize this to whatever you want
            String title = getResources().getString(R.string.app_name);  // You can either set the title to whatever you want or just use null and it will default to your app/activity name
            int color = R.color.colorPrimary; // Set the color you want to set the title to, it's a good idea to use the colorPrimary attribute

            ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(title, recentIcon);
            this.setTaskDescription(description);
        }

        ImageView home_share = findViewById(R.id.home_share);
        home_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InventoryFragment inventoryFragment = (InventoryFragment) getSupportFragmentManager().findFragmentByTag("InvFragment");
                assert inventoryFragment != null;
                inventoryFragment.shareInventory();
            }
        });
        final Fragment invFragment = InventoryFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.invContainer,invFragment,"InvFragment").commit();
        FloatingActionButton fab = findViewById(R.id.invfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InventoryFragment inventoryFragment = (InventoryFragment) getSupportFragmentManager().findFragmentByTag("InvFragment");
                assert inventoryFragment != null;
                inventoryFragment.addMedicine();
            }
        });

    }


}
