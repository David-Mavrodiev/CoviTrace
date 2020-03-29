package com.covitrack.david.covitrack.utils

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity

class PermissionManager(val activity: AppCompatActivity, val permissions: Array<String>) {
    private val RECORD_REQUEST_CODE = 101;

    fun isPermissionsGranted(): Boolean {
        for (permission in permissions) {
            val result: Int = activity.checkCallingOrSelfPermission(permission);
            if (PackageManager.PERMISSION_DENIED == result) {
                // Current needed permission is not granted
                return false;
            }
        }

        return true;
    }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(activity, permissions, RECORD_REQUEST_CODE);
    }
}