package com.covitrack.david.covitrack.utils;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

public class Utils {
    public static ActivityManager.RunningServiceInfo getRunningServiceInfoByName(Context context, String serviceName) {
        boolean serviceRunning = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos =
                manager.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> iterator = runningServiceInfos.iterator();

        while (iterator.hasNext()) {
            ActivityManager.RunningServiceInfo serviceInfo =
                    iterator.next();

            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return serviceInfo;
            }
        }

        return null;
    }

    public static void showToast(Context context, String content, int duration) {
        Toast toast = Toast.makeText(context, content, duration);
        toast.show();
    }

    public static void showSnackbar(View context, String content, int duration) {
        Snackbar snackbar = Snackbar.make(context, content, duration);
        snackbar.show();
    }

    public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setMessage(message);
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        return progress;
    }

    public static boolean isForegroundServiceRunning(Context context, String serviceName) {
        ActivityManager.RunningServiceInfo runningServiceInfo =
                Utils.getRunningServiceInfoByName(context, serviceName);

        if (runningServiceInfo != null && runningServiceInfo.foreground) {
            return true;
        }

        return false;
    }
}
