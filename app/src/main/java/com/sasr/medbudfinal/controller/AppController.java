package com.sasr.medbudfinal.controller;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class AppController extends Application {
    public static final String CHANNEL_1_ID = "notiChannel1";
    private static Context mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        if (mInstance == null) {
            mInstance = getApplicationContext();
        }
        createNotificationChannels();
    }

    public static Context getInstance() {
        return mInstance;
    }

    public void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Notification channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("Notifies the user when it is time to take scheduled medicine");
            channel1.setBypassDnd(true);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null)
                manager.createNotificationChannel(channel1);
        }
    }
}
