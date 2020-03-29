package com.covitrack.david.covitrack;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    public static final String CHANNEL_ID = "background-tracking-service";

    /**
     * Create background service notification channel for devices
     * with greater or equal version than OREO
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
               CHANNEL_ID,
               "Location tracking service",
               NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
