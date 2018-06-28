package com.graypn.permissionmaster.model;

import android.support.annotation.NonNull;

/**
 * If a permission was denied, an instance of this class will be returned
 * in the callback.
 */
public final class PermissionDeniedResponse {

    private final PermissionRequest requestedPermission;
    private final boolean permanentlyDenied;

    private PermissionDeniedResponse(@NonNull PermissionRequest requestedPermission, boolean permanentlyDenied) {
        this.requestedPermission = requestedPermission;
        this.permanentlyDenied = permanentlyDenied;
    }

    public static PermissionDeniedResponse from(@NonNull String permission, boolean permanentlyDenied) {
        return new PermissionDeniedResponse(new PermissionRequest(permission), permanentlyDenied);
    }

    public PermissionRequest getRequestedPermission() {
        return requestedPermission;
    }

    public String getPermissionName() {
        return requestedPermission.getName();
    }

    public boolean isPermanentlyDenied() {
        return permanentlyDenied;
    }
}
