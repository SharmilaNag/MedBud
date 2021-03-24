package com.sasr.medbudfinal.view.info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import com.sasr.medbudfinal.R;

public class MyInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
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

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.myInfoContainer,new MyInfoFragment(),"MyInfo").commit();
    }
}
