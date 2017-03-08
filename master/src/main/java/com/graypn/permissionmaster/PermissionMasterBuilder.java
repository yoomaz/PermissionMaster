package com.graypn.permissionmaster;

import com.graypn.permissionmaster.listener.MultiplePermissionsListener;
import com.graypn.permissionmaster.listener.PermissionListener;

import java.util.Collection;

/**
 * Created by ZhuLei on 2017/3/6.
 * Email: zhuleineuq@gmail.com
 */

public interface PermissionMasterBuilder {

    void check();

    interface Permission {
        PermissionMasterBuilder.SinglePermissionListener withPermission(String permission);

        PermissionMasterBuilder.MultiPermissionListener withPermissions(String... permissions);

        PermissionMasterBuilder.MultiPermissionListener withPermissions(Collection<String> permissions);
    }

    interface SinglePermissionListener {
        PermissionMasterBuilder withListener(PermissionListener listener);
    }

    interface MultiPermissionListener {
        PermissionMasterBuilder withListener(MultiplePermissionsListener listener);
    }


}
