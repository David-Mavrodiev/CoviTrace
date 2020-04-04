package com.covitrack.david.covitrack.base;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.covitrack.david.covitrack.R;

import java.util.Locale;

public class MultilingualBaseActivity extends AppCompatActivity {
    private Button button;
    private static final String SYSTEM_SETTINGS_KEY = "Settings";
    private static final String SYSTEM_LANGUAGE_KEY = "Language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
    }

    public void openLanguageSelection() {
        final String[] languages = { "English", "Български" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.lang_dialog_title));

        int preselectedLanguage = 0;

        String currentLanguage = getSharedPreferences("Settings", MODE_PRIVATE)
                .getString("Language", null);

        if (currentLanguage != null && !currentLanguage.equals("en")) {
            preselectedLanguage = 1;
        }

        builder.setSingleChoiceItems(languages, preselectedLanguage,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if (position == 0) {
                    setLocale("en");
                }

                if (position == 1) {
                    setLocale("bg");
                }

                recreate();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setLanguageButtonControlOnCreate(Button button) {
        this.button = button;
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLanguageSelection();
            }
        });
    }

    public void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);

        DisplayMetrics metrics = getBaseContext()
                .getResources()
                .getDisplayMetrics();

        getBaseContext()
                .getResources()
                .updateConfiguration(configuration, metrics);

        SharedPreferences.Editor editor =
                getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("Language", language);
        editor.apply();
    }

    public void loadLocale() {
        SharedPreferences preferences =
                getSharedPreferences("Settings", MODE_PRIVATE);
        String selectedLanguage =
                preferences.getString("Language", null);

        if (selectedLanguage != null) {
            setLocale(selectedLanguage);
        }
    }
}
