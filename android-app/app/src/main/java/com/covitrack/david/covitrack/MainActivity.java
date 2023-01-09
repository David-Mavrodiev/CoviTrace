package com.covitrack.david.covitrack;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.covitrack.david.covitrack.api.CovitraceApiService;
import com.covitrack.david.covitrack.api.models.requests.SendLocationRequestModel;
import com.covitrack.david.covitrack.api.models.requests.StatusRequestModel;
import com.covitrack.david.covitrack.base.MultilingualBaseActivity;
import com.covitrack.david.covitrack.models.UserStatusNavigationData;
import com.covitrack.david.covitrack.models.UserStatusType;
import com.covitrack.david.covitrack.tracking.BackgroundTrackingService;
import com.covitrack.david.covitrack.utils.Constants;
import com.covitrack.david.covitrack.utils.IdentityManager;
import com.covitrack.david.covitrack.utils.PermissionHelper;
import com.covitrack.david.covitrack.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends MultilingualBaseActivity implements Dialog.DialogListener {
    // Broadcast communication flags
    public static final String STOP_TRACKING_SERVICE_FLAG = "stop-tracking";

    private ArrayList<String> permissions = new ArrayList<>();
    private SharedPreferences preferences;
    private ProgressDialog progress = null;
    private IdentityManager identityManager;
    private CovitraceApiService apiService;

    private Handler handler = new Handler();

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

        // If user set state on his own, then redirect to status activity
        int status_flag = this.preferences.getInt(Constants.USER_STATE_STATUS, -1);
        if (status_flag == UserStatusType.CONTACT.getValue()) {
            this.redirectToUserStatusActivity(UserStatusType.CONTACT);
        } else if (status_flag == UserStatusType.INFECTED.getValue()) {
            this.redirectToUserStatusActivity(UserStatusType.INFECTED);
        }

        this.identityManager = new IdentityManager(this.preferences);

        this.apiService = new CovitraceApiService();

        this.registerLocationBroadcastReceiver();

        this.setPersistedState();

        PermissionHelper permissionHelper =
                new PermissionHelper(this, permissions);

        if (!permissionHelper.isPermissionsGranted()) {
            permissionHelper.requestAllPermissions();
        }

        // When this button is pressed, then language dialog pops up
        // and the user can select language
        Button chooseLanguage = findViewById(R.id.language);
        this.setLanguageButtonControlOnCreate(chooseLanguage);

        supportStartPostponedEnterTransition();
        supportPostponeEnterTransition();

        // Start sending location on equal intervals
        this.handler.post(periodicStatusUpdate);
    }

    /**
     * Reinitialize application, if the service is still
     * running then set the properly state
     */
    public void setPersistedState() {
        // Set tracking button state
        ToggleButton button = findViewById(R.id.togglebutton);

        Boolean isTrackingEnabled =
                Utils.isForegroundServiceRunning(this, BackgroundTrackingService.class.getName());

        button.setChecked(isTrackingEnabled);

        // Register listener again in order to
        // communicate with the service
        if (isTrackingEnabled) {
            this.registerLocationBroadcastReceiver();
        }

        TextView lastKnownLocationTextView = findViewById(R.id.real_time_location);

        String lastKnownLocation =
                this.preferences.getString(Constants.PERSISTED_LAST_KNOWN_LOCATION, null);

        if (lastKnownLocation != null) {
            lastKnownLocationTextView.setText(lastKnownLocation);
        }

        this.registerControlListeners();
    }

    public void registerControlListeners() {
        Button button = this.findViewById(R.id.help);
        button.setOnClickListener(v -> MainActivity.this.redirectToHelp());
    }

    public void onInfectedButtonClicked(View view) {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putInt(Constants.USER_STATE_STATUS, UserStatusType.INFECTED.getValue());
        editor.apply();

        String content = getString(R.string.confirm_infected);
        this.openConfirmationDialog(Constants.INFECTED_CONFIRMATION, content);
    }

    public void onContactButtonClicked(View view) {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putInt(Constants.USER_STATE_STATUS, UserStatusType.CONTACT.getValue());
        editor.apply();

        String content = getString(R.string.confirm_contact);
        this.openConfirmationDialog(Constants.CONTACTED_CONFIRMATION, content);
    }

    public void openConfirmationDialog(String action, String content) {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setAction(action);
        confirmationDialog.setContent(content);
        confirmationDialog.show(getSupportFragmentManager(), "Dialog");
    }

    public void onTrackingToggleClicked(View view) {
        ToggleButton toggleButton = (ToggleButton) view;

        if (toggleButton.isChecked()) {
            this.startTrackingService();
        } else {
            this.stopTrackingService();
        }
    }

    private Callback<SendLocationRequestModel> sendLocationCallback =
            new Callback<SendLocationRequestModel>() {
        @Override
        public void onResponse(Call<SendLocationRequestModel> call,
                               Response<SendLocationRequestModel> response) {
            stopLoading();

            if (!response.isSuccessful()) {
                View view = findViewById(R.id.main_activity_view);
                Utils.showSnackbar(view,
                        getString(R.string.failed_send_location), Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onFailure(Call<SendLocationRequestModel> call, Throwable t) {
            stopLoading();
            View view = findViewById(R.id.main_activity_view);
            Utils.showSnackbar(view,
                    getString(R.string.failed_send_location), Toast.LENGTH_SHORT);
        }
    };

    private Runnable periodicStatusUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicStatusUpdate, 10 * 1000 - SystemClock.elapsedRealtime() % 1000);
            apiService.getStatus(identityManager.getUniqueId(), new Callback<StatusRequestModel>() {
                @Override
                public void onResponse(Call<StatusRequestModel> call, Response<StatusRequestModel> response) {
                    if (!response.isSuccessful()) {
                        View view = findViewById(R.id.main_activity_view);
                        Utils.showSnackbar(view,
                                getString(R.string.failed_to_get_status),
                                Snackbar.LENGTH_LONG);
                        return;
                    }

                    UserStatusType status = UserStatusType.NONE;
                    if (response.body().isInfected()) {
                        status = UserStatusType.INFECTED;
                    } else if (response.body().isContacted()) {
                        status = UserStatusType.CONTACT;
                    }

                    receiveStatus(status);
                }

                @Override
                public void onFailure(Call<StatusRequestModel> call, Throwable t) {
                    View view = findViewById(R.id.main_activity_view);
                    Utils.showSnackbar(view,
                            getString(R.string.failed_to_get_status),
                            Snackbar.LENGTH_LONG);
                }
            });
        }
    };

    private Callback<StatusRequestModel> sendStatusCallback =
            new Callback<StatusRequestModel>() {
                @Override
                public void onResponse(Call<StatusRequestModel> call,
                                       Response<StatusRequestModel> response) {
                    stopLoading();

                    if (!response.isSuccessful()) {
                        View view = findViewById(R.id.main_activity_view);
                        Utils.showSnackbar(view,
                                getString(R.string.failed_to_send_status),
                                Snackbar.LENGTH_LONG);
                        return;
                    }

                    try {
                        StatusRequestModel model = response.body();
                        UserStatusType status = UserStatusType.CONTACT;

                        if (model.isInfected()) {
                            status = UserStatusType.INFECTED;
                        }

                        redirectToUserStatusActivity(status);
                    } catch (Exception e) {
                        Log.d("API", "Error while retrieving data.");
                    }
                }

                @Override
                public void onFailure(Call<StatusRequestModel> call, Throwable t) {
                    stopLoading();

                    View view = findViewById(R.id.main_activity_view);
                    Utils.showSnackbar(view,
                            getString(R.string.failed_to_send_status),
                            Snackbar.LENGTH_LONG);
                }
            };

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action");

            if (action.equals("location")) {
                receiveLocation(intent);
            }
        }
    };

    private void receiveStatus(UserStatusType status) {
        if (status == UserStatusType.NONE) {
            return;
        }

        Button animatedButton = null;
        ImageView imageView = findViewById(R.id.statusIcon);

        PropertyValuesHolder scaleX =
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1.5f, 1f);
        PropertyValuesHolder scaleY =
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1.5f, 1f);
        PropertyValuesHolder alpha =
                PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f);

        if (status == UserStatusType.INFECTED) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.covid));
            animatedButton = findViewById(R.id.infectedButton);
        } else if (status == UserStatusType.CONTACT) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.contact));
            animatedButton = findViewById(R.id.contactButton);
        }

        ObjectAnimator fadeInIcon =
                ObjectAnimator.ofPropertyValuesHolder(imageView, scaleX, scaleY, alpha)
                .setDuration(5000);

        ObjectAnimator popupButton =
                ObjectAnimator.ofPropertyValuesHolder(animatedButton, scaleX, scaleY)
                .setDuration(5000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fadeInIcon).before(popupButton);
        animatorSet.start();
    }

    private void receiveLocation(Intent intent) {
        Bundle bundle = intent.getBundleExtra("location-data");
        Location lastKnownLocation = (Location) bundle.getParcelable("location");
        Boolean isTrackingEnabled =
                Utils.isForegroundServiceRunning(MainActivity.this,
                        BackgroundTrackingService.class.getName());

        if (lastKnownLocation != null && isTrackingEnabled) {
            TextView textView = findViewById(R.id.real_time_location);
            double longitude = lastKnownLocation.getLongitude();
            double latitude = lastKnownLocation.getLatitude();

            String locationFormattedText = String.format("[%s, %s]",
                    longitude, latitude);

            // Persist formatted location
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString(Constants.PERSISTED_LAST_KNOWN_LOCATION, locationFormattedText);
            edit.apply();

            apiService.sendLocation(identityManager.getUniqueId(),
                    latitude, longitude, sendLocationCallback);

            textView.setText(locationFormattedText);
        }
    }

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

    /**
     * If user set some state, for example that
     * he/she is infected or contact to other person
     */
    private void redirectToUserStatusActivity(UserStatusType status) {
        Intent redirectIntent =
                new Intent(getApplicationContext(), UserStatusActivity.class);
        UserStatusNavigationData statusData = new UserStatusNavigationData(status);
        redirectIntent.putExtra(Constants.USER_STATUS, statusData);
        startActivity(redirectIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void redirectToHelp() {
        Intent redirectIntent =
                new Intent(getApplicationContext(), GettingStartedActivity.class);
        startActivity(redirectIntent);
    }

    @Override
    public void apply(Boolean state, String action) {
        if (!state) {
            return;
        }

        this.startLoading();

        if (Constants.CONTACTED_CONFIRMATION.equals(action)) {
            Log.i("API", "Send contacted status...");
            this.apiService.sendStatus(this.identityManager.getUniqueId(),
                    Constants.CONTACTED, this.sendStatusCallback);
        } else if (Constants.INFECTED_CONFIRMATION.equals(action)) {
            Log.i("API", "Send infected status...");
            this.apiService.sendStatus(this.identityManager.getUniqueId(),
                    Constants.INFECTED, this.sendStatusCallback);
        }
    }

    private void stopLoading() {
        if (this.progress != null) {
            this.progress.dismiss();
            this.progress = null;
        }
    }

    private void startLoading() {
        this.progress =
                Utils.showProgressDialog(this, "Send your status");
    }
}
