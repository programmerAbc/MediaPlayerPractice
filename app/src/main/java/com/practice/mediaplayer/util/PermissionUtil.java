package com.practice.mediaplayer.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaofeng on 2017-02-16.
 */

public class PermissionUtil {
    private static final String TAG = PermissionUtil.class.getSimpleName();
    public static final int REQUEST_PERMISSION_CODE = 0x01;

    public static boolean requestPermission(Activity activity, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = PackageManager.PERMISSION_DENIED;
            for (String permission : permissions) {
                result = ContextCompat.checkSelfPermission(activity, permission);
                if (result == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION_CODE);
                    return false;
                }
            }
        }
        return true;
    }

    public static String[] handleRequestPermissionResult(String[] permissions, int[] result) {
        List<String> deniedPermissions = new LinkedList<>();
        for (int i = 0; i < result.length; ++i) {
            if (result[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        if (deniedPermissions.isEmpty()) {
            return null;
        }
        String[] deniedPermissionArray = new String[deniedPermissions.size()];
        for (int i = 0; i < deniedPermissionArray.length; ++i) {
            deniedPermissionArray[i] = deniedPermissions.get(i);
        }
        return deniedPermissionArray;
    }
}
