package com.graypn.permissionmaster.model;

import java.util.LinkedList;
import java.util.List;

/**
 * An in detail report of the request permission process
 */
public final class MultiplePermissionsReport {

    private final List<PermissionGrantedResponse> grantedPermissionResponses;
    private final List<PermissionDeniedResponse> deniedPermissionResponses;

    public MultiplePermissionsReport() {
        grantedPermissionResponses = new LinkedList<>();
        deniedPermissionResponses = new LinkedList<>();
    }

    /**
     * Returns a collection with all the permissions that has been granted
     */
    public List<PermissionGrantedResponse> getGrantedPermissionResponses() {
        return grantedPermissionResponses;
    }

    /**
     * Returns a collection with all the permissions that has been denied
     */
    public List<PermissionDeniedResponse> getDeniedPermissionResponses() {
        return deniedPermissionResponses;
    }

    /**
     * Returns whether the user has granted all the requested permission
     */
    public boolean areAllPermissionsGranted() {
        return deniedPermissionResponses.isEmpty();
    }

    /**
     * Returns whether the user has permanently denied any of the requested permissions
     */
    public boolean isAnyPermissionPermanentlyDenied() {
        boolean hasPermanentlyDeniedAnyPermission = false;

        for (PermissionDeniedResponse deniedResponse : deniedPermissionResponses) {
            if (deniedResponse.isPermanentlyDenied()) {
                hasPermanentlyDeniedAnyPermission = true;
                break;
            }
        }

        return hasPermanentlyDeniedAnyPermission;
    }

    public boolean addGrantedPermissionResponse(PermissionGrantedResponse response) {
        return grantedPermissionResponses.add(response);
    }

    public boolean addDeniedPermissionResponse(PermissionDeniedResponse response) {
        return deniedPermissionResponses.add(response);
    }

    public void clear() {
        grantedPermissionResponses.clear();
        deniedPermissionResponses.clear();
    }
}
