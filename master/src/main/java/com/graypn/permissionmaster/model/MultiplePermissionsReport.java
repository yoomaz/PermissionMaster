package com.graypn.permissionmaster.model;

import java.util.LinkedList;
import java.util.List;

/**
 * 多个权限请求结果
 */
public final class MultiplePermissionsReport {

    private final List<PermissionResponse> grantedPermissionResponses;
    private final List<PermissionResponse> deniedPermissionResponses;

    public MultiplePermissionsReport() {
        grantedPermissionResponses = new LinkedList<>();
        deniedPermissionResponses = new LinkedList<>();
    }

    /**
     * 获取授予的权限
     */
    public List<PermissionResponse> getGrantedPermissionResponses() {
        return grantedPermissionResponses;
    }

    /**
     * 获取拒绝的权限
     */
    public List<PermissionResponse> getDeniedPermissionResponses() {
        return deniedPermissionResponses;
    }

    /**
     * 是否所有权限被授予
     */
    public boolean areAllPermissionsGranted() {
        return deniedPermissionResponses.isEmpty();
    }

    /**
     * 是否有一个权限永久被拒绝
     */
    public boolean isAnyPermissionPermanentlyDenied() {
        boolean hasPermanentlyDeniedAnyPermission = false;

        for (PermissionResponse deniedResponse : deniedPermissionResponses) {
            if (deniedResponse.isPermanentlyDenied()) {
                hasPermanentlyDeniedAnyPermission = true;
                break;
            }
        }

        return hasPermanentlyDeniedAnyPermission;
    }

    public void addGrantedPermissionResponse(PermissionResponse response) {
        grantedPermissionResponses.add(response);
    }

    public void addDeniedPermissionResponse(PermissionResponse response) {
        deniedPermissionResponses.add(response);
    }

    public void clear() {
        grantedPermissionResponses.clear();
        deniedPermissionResponses.clear();
    }
}
