package com.covitrack.david.covitrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.covitrack.david.covitrack.api.CovitraceApiService;
import com.covitrack.david.covitrack.api.models.requests.StatusRequestModel;
import com.covitrack.david.covitrack.base.MultilingualBaseActivity;
import com.covitrack.david.covitrack.models.UserStatusNavigationData;
import com.covitrack.david.covitrack.models.UserStatusType;
import com.covitrack.david.covitrack.utils.Constants;
import com.covitrack.david.covitrack.utils.IdentityManager;
import com.covitrack.david.covitrack.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserStatusActivity extends MultilingualBaseActivity implements Dialog.DialogListener {
    private static final int BUTTON_WIDTH_IN_PIXELS = 450;
    private static final int BUTTON_HEIGHT_IN_PIXELS = 350;
    private static final int TEXT_SIZE = 15;
    private CovitraceApiService apiService;
    private SharedPreferences preferences;
    private IdentityManager identityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);

        this.apiService = new CovitraceApiService();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.identityManager = new IdentityManager(this.preferences);

        this.initialize();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initialize() {
        String message = null;
        UserStatusNavigationData data =
                getIntent().getParcelableExtra(Constants.USER_STATUS);
        UserStatusType status = data.getStatus();
        TextView textView = this.findViewById(R.id.userStatusTextView);

        RelativeLayout layout = findViewById(R.id.dynamic_grid_layout);

        if (UserStatusType.CONTACT.equals(status)) {
            message = getString(R.string.user_contact_status);
            this.createContactLayout(layout);
        } else if (UserStatusType.INFECTED.equals(status)) {
            message = getString(R.string.user_infected_status);
            this.createInfectedLayout(layout);
        }

        textView.setText(message);
    }

    private void createContactLayout(RelativeLayout layout) {
        Map<Integer, Integer> negativeLayoutRules = new HashMap<>();
        negativeLayoutRules.put(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        Button negativeButton = this.createButton(R.string.user_negative,
                R.color.colorPrimary, R.drawable.stroke_button_style, negativeLayoutRules);

        Map<Integer, Integer> positiveLayoutRules = new HashMap<>();
        positiveLayoutRules.put(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        Button positiveButton = this.createButton(R.string.user_positive,
                R.color.colorWhite, R.drawable.fill_button_style, positiveLayoutRules);

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfirmationDialog(Constants.CONTACTED_NEGATIVE_CONFIRMATION,
                        getString(R.string.confirm_contact_negative));
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfirmationDialog(Constants.CONTACTED_POSITIVE_CONFIRMATION,
                        getString(R.string.confirm_contact_positive));
            }
        });

        layout.addView(negativeButton);
        layout.addView(positiveButton);
    }

    private void createInfectedLayout(RelativeLayout layout) {
        Button healedButton = new Button(this);
        healedButton.setText(R.string.user_healed);
        healedButton.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        healedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfirmationDialog(Constants.HEALED_CONFIRMATION,
                        getString(R.string.confirm_healed));
            }
        });

        layout.addView(healedButton);
    }

    private Button createButton(int stringId, int textColorId, int drawableId, Map<Integer, Integer> layoutRules) {
        Button button = new Button(this);
        button.setBackground(getResources()
                .getDrawable(drawableId));
        button.setText(getString(stringId));
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setTextColor(getResources().getColor(textColorId));
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        button.setTextSize(TEXT_SIZE);
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layout.width = BUTTON_WIDTH_IN_PIXELS;
        layout.height = BUTTON_HEIGHT_IN_PIXELS;

        for (Map.Entry<Integer, Integer> rule : layoutRules.entrySet()) {
            layout.addRule(rule.getKey(), rule.getValue());
        }

        button.setLayoutParams(layout);
        return button;
    }

    private void changeStatusActivity(UserStatusType status) {
        Intent redirectIntent =
                new Intent(getApplicationContext(), UserStatusActivity.class);
        UserStatusNavigationData statusData = new UserStatusNavigationData(status);
        redirectIntent.putExtra(Constants.USER_STATUS, statusData);
        startActivity(redirectIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void redirectGeneralActivity() {
        Intent redirectIntent =
                new Intent(getApplicationContext(), MainActivity.class);
        startActivity(redirectIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void deleteLocalStatus() {
        this.preferences.edit().remove(Constants.USER_STATE_STATUS).apply();
    }

    private void setLocalStatus(int statusFlag) {
        this.preferences.edit().putInt(Constants.USER_STATE_STATUS, statusFlag).apply();
    }

    private Callback<StatusRequestModel> negativeCallback = new Callback<StatusRequestModel>() {
        @Override
        public void onResponse(Call<StatusRequestModel> call,
                               Response<StatusRequestModel> response) {
            if (!response.isSuccessful()) {
                View activityView = findViewById(R.id.user_status_activity);
                Utils.showSnackbar(activityView,
                        getString(R.string.failed_to_send_status),
                        Snackbar.LENGTH_LONG);
                return;
            }

            deleteLocalStatus();
            redirectGeneralActivity();
        }

        @Override
        public void onFailure(Call<StatusRequestModel> call, Throwable t) {
            View activityView = findViewById(R.id.user_status_activity);
            Utils.showSnackbar(activityView,
                    getString(R.string.failed_to_send_status),
                    Snackbar.LENGTH_LONG);
        }
    };

    private Callback<StatusRequestModel> positiveCallback = new Callback<StatusRequestModel>() {
        @Override
        public void onResponse(Call<StatusRequestModel> call, Response<StatusRequestModel> response) {
            if (!response.isSuccessful()) {
                View activityView = findViewById(R.id.user_status_activity);
                Utils.showSnackbar(activityView,
                        getString(R.string.failed_to_send_status),
                        Snackbar.LENGTH_LONG);
                return;
            }

            setLocalStatus(UserStatusType.INFECTED.getValue());
            changeStatusActivity(UserStatusType.INFECTED);
        }

        @Override
        public void onFailure(Call<StatusRequestModel> call, Throwable t) {
            View activityView = findViewById(R.id.user_status_activity);
            Utils.showSnackbar(activityView,
                    getString(R.string.failed_to_send_status),
                    Snackbar.LENGTH_LONG);
        }
    };

    public void openConfirmationDialog(String action, String content) {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setAction(action);
        confirmationDialog.setContent(content);
        confirmationDialog.show(getSupportFragmentManager(), "Dialog");
    }

    @Override
    public void apply(Boolean state, String action) {
        if (!state) {
            return;
        }

        if (Constants.CONTACTED_NEGATIVE_CONFIRMATION.equals(action) ||
                Constants.HEALED_CONFIRMATION.equals(action)) {
            this.apiService.sendStatus(this.identityManager.getUniqueId(),
                    UserStatusType.NONE.getValue(), this.negativeCallback);
        } else if (Constants.CONTACTED_POSITIVE_CONFIRMATION.equals(action)) {
            apiService.sendStatus(identityManager.getUniqueId(),
                    UserStatusType.INFECTED.getValue(), positiveCallback);
        }
    }
}
