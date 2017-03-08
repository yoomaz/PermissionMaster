package com.graypn.permissionmaster.model;

import android.support.annotation.NonNull;

/**
 * Wrapper class for a permission request
 */
public final class PermissionRequest {

    private final String name;

    public PermissionRequest(@NonNull String name) {
        this.name = name;
    }

    /**
     * One of the values found in {@link android.Manifest.permission}
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Permission name: " + name;
    }
}
