package com.covitrack.david.covitrack.tracking;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.covitrack.david.covitrack.MainActivity;
import com.covitrack.david.covitrack.R;
import com.covitrack.david.covitrack.api.CovitraceApiService;
import com.covitrack.david.covitrack.api.models.requests.StatusRequestModel;
import com.covitrack.david.covitrack.models.UserStatusType;
import com.covitrack.david.covitrack.utils.Constants;
import com.covitrack.david.covitrack.utils.IdentityManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.covitrack.david.covitrack.App.CHANNEL_ID;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BackgroundTrackingService extends Service implements LocationListener {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private LocalBroadcastManager broadcastManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionFlag = intent.getAction();

        if (Constants.STOP_FOREGROUND_SERVICE_FLAG.equals(actionFlag)) {
            stopForeground(true);
            stopUsingGPS();
            stopSelf();
            return START_STICKY;
        }

        String input = intent.getStringExtra("message");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);

        this.broadcastManager = LocalBroadcastManager.getInstance(this);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("COVITrace Application")
                .setContentText(input)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentIntent(pendingIntent)
                .build();

        getLocation();

        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    private void sendUserStatusToActivity(UserStatusType status) {
        Intent statusIntent = new Intent(BackgroundTrackingService.class.getName());
        statusIntent.putExtra("action", "status");
        statusIntent.putExtra("status", status.getValue());

        this.broadcastManager.sendBroadcast(statusIntent);
    }

    private void sendLocationToActivity() {
        Intent locationIntent = new Intent(BackgroundTrackingService.class.getName());
        locationIntent.putExtra("action", "location");

        Bundle bundle = new Bundle();
        bundle.putParcelable("location", this.getLocation());

        locationIntent.putExtra("location-data", bundle);

        this.broadcastManager.sendBroadcast(locationIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 5 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5; // 5 seconds

    // Declaring a Location Manager
    protected LocationManager locationManager;

    // Flag for GPS status
    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;

    // Flag for GPS status
    boolean canGetLocation = false;

    Location location; // Location
    double latitude; // Latitude
    double longitude; // Longitude

    public Boolean hasPermissions() {
        Boolean fineLocationPerm =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED;
        Boolean accessCoarsePerm =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED;

        if (fineLocationPerm && accessCoarsePerm) {
            return false;
        }

        return true;
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled && this.hasPermissions()) {
                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    } catch (SecurityException e) {
                        // TODO log the error
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled && this.hasPermissions()) {
                    if (location == null) {
                        try {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        } catch (SecurityException e) {
                            // TODO log the error
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app.
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(BackgroundTrackingService.this);
        }
    }


    /**
     * Function to get latitude
     * */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }


    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/Wi-Fi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    // Location tracking methods

    @Override
    public void onLocationChanged(Location location) {
        Log.i("GPS_PROVIDER", "New location is sent...");
        this.location = location;
        this.sendLocationToActivity();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
