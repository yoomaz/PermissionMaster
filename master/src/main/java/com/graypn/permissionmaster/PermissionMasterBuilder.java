package com.graypn.permissionmaster;

import com.graypn.permissionmaster.listener.MultiplePermissionsListener;
import com.graypn.permissionmaster.listener.SinglePermissionListener;

import java.util.Collection;

/**
 * 接口集合
 */

public interface PermissionMasterBuilder {

    void check();

    interface PermissionBuilder {
        SinglePermissionListenerBuilder withPermission(String permission);

        PermissionMasterBuilder.MultiPermissionListenerBuilder withPermissions(String... permissions);

        PermissionMasterBuilder.MultiPermissionListenerBuilder withPermissions(Collection<String> permissions);
    }

    interface SinglePermissionListenerBuilder {
        PermissionMasterBuilder withListener(SinglePermissionListener listener);
    }

    interface MultiPermissionListenerBuilder {
        PermissionMasterBuilder withListener(MultiplePermissionsListener listener);
    }


}
