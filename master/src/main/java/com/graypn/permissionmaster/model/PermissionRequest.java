package com.graypn.permissionmaster.model;

import android.support.annotation.NonNull;

/**
 * 权限请求封装
 */
public final class PermissionRequest {

    private final String name;

    public PermissionRequest(@NonNull String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Permission name: " + name;
    }
}
