package com.covitrack.david.covitrack.utils;

public class Constants {
    public static final String STOP_FOREGROUND_SERVICE_FLAG = "stop-foreground-service";

    public static final String PERSISTED_LAST_KNOWN_LOCATION = "last-known-location";

    // Confirmation modal actions
    public static final String CONTACTED_CONFIRMATION = "contacted-confirmation";
    public static final String INFECTED_CONFIRMATION = "infected-confirmation";
    public static final String CONTACTED_NEGATIVE_CONFIRMATION =
            "contacted-negative-confirmation";
    public static final String CONTACTED_POSITIVE_CONFIRMATION =
            "contacted-positive-confirmation";
    public static final String HEALED_CONFIRMATION =
            "healed-confirmation";

    //API
    public static final int CONTACTED = 1;
    public static final int INFECTED = 2;

    //NAVIGATION
    public static final String USER_STATUS = "UserStatusIntent";

    //Preferences keys
    public static final String USER_STATE_STATUS = "user-status";
}
