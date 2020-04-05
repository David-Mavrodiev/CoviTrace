package com.covitrack.david.covitrack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.covitrack.david.covitrack.adapters.GettingStartedViewsAdapter;
import com.covitrack.david.covitrack.base.MultilingualBaseActivity;
import com.covitrack.david.covitrack.models.UserStatusNavigationData;
import com.covitrack.david.covitrack.models.UserStatusType;
import com.covitrack.david.covitrack.utils.Constants;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class UserStatusActivity extends MultilingualBaseActivity {
    private static final int BUTTON_WIDTH_IN_PIXELS = 450;
    private static final int BUTTON_HEIGHT_IN_PIXELS = 350;
    private static final int TEXT_SIZE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);

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
                R.drawable.stroke_button_style, negativeLayoutRules);

        Map<Integer, Integer> positiveLayoutRules = new HashMap<>();
        positiveLayoutRules.put(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        Button positiveButton = this.createButton(R.string.user_positive,
                R.drawable.fill_button_style, positiveLayoutRules);

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

        layout.addView(healedButton);
    }

    private Button createButton(int stringId, int drawableId, Map<Integer, Integer> layoutRules) {
        Button button = new Button(this);
        button.setBackground(getResources()
                .getDrawable(drawableId));
        button.setText(getString(stringId));
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
}
