package com.covitrack.david.covitrack;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.covitrack.david.covitrack.adapters.GettingStartedViewsAdapter;
import com.covitrack.david.covitrack.base.MultilingualBaseActivity;

public class GettingStartedActivity extends MultilingualBaseActivity {
    private TextView[] indicators;
    private GettingStartedViewsAdapter gettingStartedViewsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getting_started);

        ViewPager pager = findViewById(R.id.viewPager);

        this.gettingStartedViewsAdapter =
                new GettingStartedViewsAdapter(this);

        int count = gettingStartedViewsAdapter.getCount();

        pager.addOnPageChangeListener(this.onPageChangeListener);
        pager.setAdapter(gettingStartedViewsAdapter);

        this.initializeIndicators(0, count);
    }

    public void initializeIndicators(int selectedPosition, int count) {
        this.indicators = new TextView[count];

        LinearLayout indicatorContainer = this.findViewById(R.id.indicators);
        indicatorContainer.removeAllViews();

        for (int i = 0; i < count; i++) {
            indicators[i] = new TextView(this);
            indicators[i].setText(Html.fromHtml("&#8226;"));
            indicators[i].setTextSize(40);
            indicators[i].setTextColor(getResources().getColor(R.color.colorPrimary));

            indicatorContainer.addView(indicators[i]);
        }

        if (selectedPosition >= 0) {
            indicators[selectedPosition]
                    .setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            int count =
                    GettingStartedActivity.this.gettingStartedViewsAdapter.getCount();
            GettingStartedActivity.this.initializeIndicators(position, count);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
