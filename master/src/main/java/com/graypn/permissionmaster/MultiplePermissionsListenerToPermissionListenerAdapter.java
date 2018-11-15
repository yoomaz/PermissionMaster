package com.graypn.permissionmaster;

import com.graypn.permissionmaster.listener.MultiplePermissionsListener;
import com.graypn.permissionmaster.listener.SinglePermissionListener;
import com.graypn.permissionmaster.model.MultiplePermissionsReport;
import com.graypn.permissionmaster.model.PermissionResponse;

import java.util.List;

/**
 * 多权限转为单权限适配器
 */
class MultiplePermissionsListenerToPermissionListenerAdapter implements MultiplePermissionsListener {

    private final SinglePermissionListener listener;

    MultiplePermissionsListenerToPermissionListenerAdapter(SinglePermissionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {
        List<PermissionResponse> deniedResponses = report.getDeniedPermissionResponses();
        List<PermissionResponse> grantedResponses = report.getGrantedPermissionResponses();

        if (!deniedResponses.isEmpty()) {
            PermissionResponse response = deniedResponses.get(0);
            listener.onPermissionDenied(response);
        } else {
            PermissionResponse response = grantedResponses.get(0);
            listener.onPermissionGranted(response);
        }
    }
}
