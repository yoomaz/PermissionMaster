package com.graypn.permissionmaster;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZhuLei on 2017/3/6.
 * Email: zhuleineuq@gmail.com
 */

class PermissionMasterActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PermissionMaster.onActivityReady(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        PermissionMaster.onActivityReady(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        List<String> grantedPermissions = new LinkedList<>();
        List<String> deniedPermissions = new LinkedList<>();

        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            } else {
                grantedPermissions.add(permission);
            }
        }

        PermissionMaster.onPermissionsRequested(grantedPermissions, deniedPermissions);
    }
}
