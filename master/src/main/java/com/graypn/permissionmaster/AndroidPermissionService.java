package com.graypn.permissionmaster;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 封装一些和操作系统的权限操作
 */
class AndroidPermissionService {

    /**
     * 检查权限
     */
    int checkSelfPermission(@NonNull Context context, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission);
    }

    /**
     * 申请权限
     */
    void requestPermissions(@Nullable Activity activity, @NonNull String[] permissions, int requestCode) {
        if (activity == null) {
            return;
        }

        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    /**
     * 是否永远拒绝权限
     */
    boolean shouldShowRequestPermissionRationale(@Nullable Activity activity, @NonNull String permission) {
        return activity != null
                && ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }
}
