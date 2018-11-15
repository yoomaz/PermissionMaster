package com.graypn.permissionmaster.model;

import android.support.annotation.NonNull;

/**
 * 权限拒绝响应
 */
public final class PermissionResponse {

    private final PermissionRequest requestedPermission;
    private boolean permanentlyDenied;

    public PermissionResponse(PermissionRequest requestedPermission) {
        this.requestedPermission = requestedPermission;
    }

    private PermissionResponse(@NonNull PermissionRequest requestedPermission, boolean permanentlyDenied) {
        this.requestedPermission = requestedPermission;
        this.permanentlyDenied = permanentlyDenied;
    }

    public static PermissionResponse from(@NonNull String permission) {
        return new PermissionResponse(new PermissionRequest(permission));
    }

    public static PermissionResponse from(@NonNull String permission, boolean permanentlyDenied) {
        return new PermissionResponse(new PermissionRequest(permission), permanentlyDenied);
    }

    public String getPermissionName() {
        return requestedPermission.getName();
    }

    public boolean isPermanentlyDenied() {
        return permanentlyDenied;
    }
}
