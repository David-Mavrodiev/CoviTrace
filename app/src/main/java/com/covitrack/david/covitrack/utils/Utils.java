package com.covitrack.david.covitrack.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

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

    public static boolean isForegroundServiceRunning(Context context, String serviceName) {
        ActivityManager.RunningServiceInfo runningServiceInfo =
                Utils.getRunningServiceInfoByName(context, serviceName);

        if (runningServiceInfo != null && runningServiceInfo.foreground) {
            return true;
        }

        return false;
    }
}
