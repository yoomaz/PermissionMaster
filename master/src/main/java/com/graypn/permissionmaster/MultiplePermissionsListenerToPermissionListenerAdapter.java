package com.graypn.permissionmaster;

import com.graypn.permissionmaster.listener.MultiplePermissionsListener;
import com.graypn.permissionmaster.listener.PermissionListener;
import com.graypn.permissionmaster.model.MultiplePermissionsReport;
import com.graypn.permissionmaster.model.PermissionDeniedResponse;
import com.graypn.permissionmaster.model.PermissionGrantedResponse;

import java.util.List;

/**
 * 多权限转为单权限适配器
 *
 * @author ZhuLei
 * @date 2018/6/28
 */
class MultiplePermissionsListenerToPermissionListenerAdapter implements MultiplePermissionsListener {

    private final PermissionListener listener;

    MultiplePermissionsListenerToPermissionListenerAdapter(PermissionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {
        List<PermissionDeniedResponse> deniedResponses = report.getDeniedPermissionResponses();
        List<PermissionGrantedResponse> grantedResponses = report.getGrantedPermissionResponses();

        if (!deniedResponses.isEmpty()) {
            PermissionDeniedResponse response = deniedResponses.get(0);
            listener.onPermissionDenied(response);
        } else {
            PermissionGrantedResponse response = grantedResponses.get(0);
            listener.onPermissionGranted(response);
        }
    }
}
