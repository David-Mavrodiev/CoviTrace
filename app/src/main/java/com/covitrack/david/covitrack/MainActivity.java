package com.covitrack.david.covitrack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.covitrack.david.covitrack.tracking.BackgroundTrackingService;
import com.covitrack.david.covitrack.utils.Constants;
import com.covitrack.david.covitrack.utils.PermissionHelper;
import com.covitrack.david.covitrack.utils.PermissionManager;
import com.covitrack.david.covitrack.utils.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Broadcast communication flags
    public static final String STOP_TRACKING_SERVICE_FLAG = "stop-tracking";

    private ArrayList<String> permissions = new ArrayList<>();
    private SharedPreferences preferences;


    public MainActivity() {
        // GPS and internet permissions
        this.permissions
                .add(android.Manifest.permission.INTERNET);
        this.permissions
                .add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        this.permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.registerLocationBroadcastReceiver();

        this.setPersistedState();

        PermissionHelper permissionHelper =
                new PermissionHelper(this, permissions);

        if (!permissionHelper.isPermissionsGranted()) {
            permissionHelper.requestAllPermissions();
        }
    }

    /**
     * Reinitialize application, if the service is still
     * running then set the properly state
     */
    public void setPersistedState() {
        // Set tracking button state
        ToggleButton button =
                (ToggleButton) findViewById(R.id.togglebutton);

        Boolean isTrackingEnabled =
                Utils.isForegroundServiceRunning(this, BackgroundTrackingService.class.getName());

        button.setChecked(isTrackingEnabled);

        // Register listener again in order to
        // communicate with the service
        if (isTrackingEnabled) {
            this.registerLocationBroadcastReceiver();
        }

        TextView lastKnownLocationTextView =
                (TextView) findViewById(R.id.real_time_location);

        String lastKnownLocation =
                this.preferences.getString(Constants.PERSISTED_LAST_KNOWN_LOCATION, null);

        if (lastKnownLocation != null) {
            lastKnownLocationTextView.setText(lastKnownLocation);
        }
    }

    public void onTrackingToggleClicked(View view) {
        ToggleButton toggleButton = (ToggleButton) view;

        if (toggleButton.isChecked()) {
            this.startTrackingService();
        } else {
            this.stopTrackingService();
        }
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("location-data");
            Location lastKnownLocation = (Location) bundle.getParcelable("location");

            if (lastKnownLocation != null) {
                TextView textView = (TextView) findViewById(R.id.real_time_location);
                String locationFormattedText = String.format("Current location: [%s, %s]",
                        lastKnownLocation.getLongitude(),
                        lastKnownLocation.getLatitude());

                // Persist formatted location
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString(Constants.PERSISTED_LAST_KNOWN_LOCATION, locationFormattedText);
                edit.apply();

                textView.setText(locationFormattedText);
            }
        }
    };

    public void registerLocationBroadcastReceiver() {
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(
                    messageReceiver, new IntentFilter(BackgroundTrackingService.class.getName()));
    }

    public void startTrackingService() {
        Intent trackingServiceIntent = new Intent(this, BackgroundTrackingService.class);
        trackingServiceIntent.putExtra("message", "Tracking...");

        ContextCompat.startForegroundService(this, trackingServiceIntent);
    }

    public void stopTrackingService() {
        Log.i("TRACKING", "STOP TRACKING...");

        Intent stopTrackingIntent = new Intent(this, BackgroundTrackingService.class);
        stopTrackingIntent.setAction(Constants.STOP_FOREGROUND_SERVICE_FLAG);

        ContextCompat.startForegroundService(this, stopTrackingIntent);
    }
}
