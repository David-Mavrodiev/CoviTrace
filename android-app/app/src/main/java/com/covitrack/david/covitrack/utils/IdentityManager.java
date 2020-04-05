package com.covitrack.david.covitrack.utils;

import android.content.SharedPreferences;

import java.util.UUID;

public class IdentityManager {
    private final String COVITRACE_UNIQUE_ID = "covitrace_unique_id";
    private String uniqueId;

    public IdentityManager(SharedPreferences preferences) {
        String userId =
                preferences.getString(this.COVITRACE_UNIQUE_ID, null);

        if (userId == null) {
            userId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(this.COVITRACE_UNIQUE_ID, userId);
            editor.apply();
        }

        this.uniqueId = userId;
    }

    public String getUniqueId() {
        return uniqueId;
    }
}
