package com.graypn.permissionmaster;

import android.app.Activity;

import com.graypn.permissionmaster.listener.EmptyMultiplePermissionsListener;
import com.graypn.permissionmaster.listener.MultiplePermissionsListener;
import com.graypn.permissionmaster.listener.PermissionListener;
import com.graypn.permissionmaster.model.MultiplePermissionsReport;
import com.graypn.permissionmaster.model.PermissionDeniedResponse;
import com.graypn.permissionmaster.model.PermissionGrantedResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by ZhuLei on 2017/3/6.
 * Email: zhuleineuq@gmail.com
 */

public class PermissionMaster implements PermissionMasterBuilder,
        PermissionMasterBuilder.Permission,
        PermissionMasterBuilder.SinglePermissionListener,
        PermissionMasterBuilder.MultiPermissionListener {

    private static PermissionMasterInstance mInstance;

    private Collection<String> mPermissions;
    private MultiplePermissionsListener mMutileListener = new EmptyMultiplePermissionsListener();

    private PermissionMaster(Activity activity) {
        initialize(activity);
    }

    private void initialize(Activity activity) {
        if (mInstance == null) {
            mInstance = new PermissionMasterInstance(activity);
        } else {
            mInstance.setContext(activity);
        }
    }

    public static PermissionMasterBuilder.Permission withActivity(Activity activity) {
        return new PermissionMaster(activity);
    }

    @Override
    public SinglePermissionListener withPermission(String permission) {
        mPermissions = Collections.singletonList(permission);
        return this;
    }

    @Override
    public MultiPermissionListener withPermissions(String... permissions) {
        mPermissions = Arrays.asList(permissions);
        return this;
    }

    @Override
    public MultiPermissionListener withPermissions(Collection<String> permissions) {
        mPermissions = new ArrayList<>(permissions);
        return this;
    }

    @Override
    public PermissionMasterBuilder withListener(PermissionListener listener) {
        mMutileListener = new MultiplePermissionsListenerToPermissionListenerAdapter(listener);
        return this;
    }

    @Override
    public PermissionMasterBuilder withListener(MultiplePermissionsListener listener) {
        mMutileListener = listener;
        return this;
    }

    @Override
    public void check() {
        try {
            mInstance.checkPermissions(mMutileListener, mPermissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static void onActivityReady(Activity activity) {
        if (mInstance != null) {
            mInstance.onActivityReady(activity);
        }
    }

    static void onPermissionsRequested(Collection<String> grantedPermissions, Collection<String> deniedPermissions) {
        if (mInstance != null) {
            mInstance.updatePermissionsAsGranted(grantedPermissions);
            mInstance.updatePermissionsAsDenied(deniedPermissions);
        }
    }

    /**
     * 多权限回调到但权限回调适配器
     */
    private final class MultiplePermissionsListenerToPermissionListenerAdapter
            implements MultiplePermissionsListener {

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
}
