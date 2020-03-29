package com.covitrack.david.covitrack.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Help with checking and requesting permissions
 */
public class PermissionHelper {
    private AppCompatActivity activity;
    private ArrayList<String> permissions;
    private static final int RECORD_REQUEST_CODE = 101;

    public PermissionHelper(AppCompatActivity activity, ArrayList<String> permissions) {
        this.activity = activity;
        this.permissions = permissions;
    }

    public boolean isPermissionsGranted() {
        for (String permission : this.permissions) {
            int resultCode =
                    ContextCompat.checkSelfPermission(this.activity, permission);
            if (resultCode == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }

        return true;
    }

    public void requestAllPermissions() {
        String[] permissions = this.permissions.toArray(new String[0]);
        ActivityCompat.requestPermissions(this.activity, permissions, RECORD_REQUEST_CODE);
    }
}
