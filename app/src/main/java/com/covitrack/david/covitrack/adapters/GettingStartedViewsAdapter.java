package com.covitrack.david.covitrack.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.covitrack.david.covitrack.R;

public class GettingStartedViewsAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;

    private int[] icons = {
        R.drawable.go_out,
        R.drawable.turn_on_gps,
        R.drawable.phone_alert,
        R.drawable.send_signal
    };

    private String[] titles;
    private String[] descriptions;


    public GettingStartedViewsAdapter(Context view) {
        this.context = view;

        this.titles = new String[4];
        this.titles[0] = this.context.getString(R.string.go_out_title);
        this.titles[1] = this.context.getString(R.string.turn_on_gps_title);
        this.titles[2] = this.context.getString(R.string.check_for_signals_title);
        this.titles[3] = this.context.getString(R.string.send_signal_title);

        this.descriptions = new String[4];
        this.descriptions[0] = this.context.getString(R.string.go_out_description);
        this.descriptions[1] = this.context.getString(R.string.turn_on_gps_description);
        this.descriptions[2] = this.context.getString(R.string.check_for_signals_description);
        this.descriptions[3] = this.context.getString(R.string.send_signal_description);
    }

    @Override
    public int getCount() {
        return this.titles.length;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        this.inflater =
                (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =
            this.inflater.inflate(R.layout.getting_started_slide, container, false);

        ImageView icon = view.findViewById(R.id.slideIcon);
        icon.setImageDrawable(this.context.getResources().getDrawable(this.icons[position]));

        TextView title = view.findViewById(R.id.slideTitle);
        title.setText(this.titles[position]);

        TextView description = view.findViewById(R.id.slideDescription);
        description.setText(this.descriptions[position]);

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
