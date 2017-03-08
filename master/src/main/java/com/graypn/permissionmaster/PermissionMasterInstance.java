package com.graypn.permissionmaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.graypn.permissionmaster.listener.EmptyMultiplePermissionsListener;
import com.graypn.permissionmaster.model.MultiplePermissionsReport;
import com.graypn.permissionmaster.model.PermissionDeniedResponse;
import com.graypn.permissionmaster.model.PermissionGrantedResponse;
import com.graypn.permissionmaster.listener.MultiplePermissionsListener;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ZhuLei on 2017/3/6.
 * Email: zhuleineuq@gmail.com
 */

final class PermissionMasterInstance {

    private static final int PERMISSIONS_REQUEST_CODE = 42;
    private static final MultiplePermissionsListener EMPTY_LISTENER =
            new EmptyMultiplePermissionsListener();

    private WeakReference<Context> mContext;
    private Activity mActivity;
    private AndroidPermissionService androidPermissionService;
    private final Collection<String> mPendingPermissions;
    private final MultiplePermissionsReport mMultiplePermissionsReport;
    private MultiplePermissionsListener mMultiplePermissionsListener;

    private final AtomicBoolean mIsShowingNativeDialog;

    PermissionMasterInstance(Context context) {
        setContext(context);
        androidPermissionService = new AndroidPermissionService();
        mPendingPermissions = new TreeSet<>();
        mMultiplePermissionsReport = new MultiplePermissionsReport();
        mIsShowingNativeDialog = new AtomicBoolean();
    }

    void setContext(Context context) {
        mContext = new WeakReference<>(context);
    }

    void checkPermissions(MultiplePermissionsListener listener, Collection<String> permissions) {
        if (permissions.isEmpty()) {
            return;
        }
        if (mContext.get() == null) {
            return;
        }
        mPendingPermissions.clear();
        mPendingPermissions.addAll(permissions);
        mMultiplePermissionsReport.clear();
        mMultiplePermissionsListener = listener;
        if (isEveryPermissionGranted(permissions, mContext.get())) {
            MultiplePermissionsReport report = new MultiplePermissionsReport();
            for (String permission : permissions) {
                report.addGrantedPermissionResponse(PermissionGrantedResponse.from(permission));
            }
            listener.onPermissionsChecked(report);
        } else {
            startTransparentActivityIfNeeded();
        }
    }

    void onActivityReady(Activity activity) {
        mActivity = activity;
        PermissionStates permissionStates = null;
        if (mActivity != null) {
            permissionStates = getPermissionStates(mPendingPermissions);
        }
        if (permissionStates != null) {
            // 向系统请求没有授权的权限
            requestPermissionsToSystem(permissionStates.getDeniedPermissions());
            // 更新已经授权的权限
            updatePermissionsAsGranted(permissionStates.getGrantedPermissions());
        }
    }

    void updatePermissionsAsGranted(Collection<String> permissions) {
        for (String permission : permissions) {
            PermissionGrantedResponse response = PermissionGrantedResponse.from(permission);
            mMultiplePermissionsReport.addGrantedPermissionResponse(response);
        }
        onPermissionsChecked(permissions);
    }

    void updatePermissionsAsDenied(Collection<String> permissions) {
        for (String permission : permissions) {
            PermissionDeniedResponse response = PermissionDeniedResponse.from(permission,
                    !androidPermissionService.shouldShowRequestPermissionRationale(mActivity, permission));
            mMultiplePermissionsReport.addDeniedPermissionResponse(response);
        }
        onPermissionsChecked(permissions);
    }

    private boolean isEveryPermissionGranted(Collection<String> permissions, Context context) {
        for (String permission : permissions) {
            int permissionState = androidPermissionService.checkSelfPermission(context, permission);
            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startTransparentActivityIfNeeded() {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, PermissionMasterActivity.class);
        context.startActivity(intent);
    }

    private void onPermissionsChecked(Collection<String> permissions) {
        if (mPendingPermissions.isEmpty()) {
            return;
        }
        mPendingPermissions.removeAll(permissions);
        if (mPendingPermissions.isEmpty()) {
            mActivity.finish();
            mActivity = null;
            mIsShowingNativeDialog.set(false);
            MultiplePermissionsListener currentListener = mMultiplePermissionsListener;
            mMultiplePermissionsListener = EMPTY_LISTENER;
            currentListener.onPermissionsChecked(mMultiplePermissionsReport);
        }
    }

    /**
     * Starts the native request permissions process
     */
    private void requestPermissionsToSystem(Collection<String> permissions) {
        if (permissions.isEmpty()) {
            return;
        }
        if (!mIsShowingNativeDialog.get()) {
            androidPermissionService.requestPermissions(mActivity,
                    permissions.toArray(new String[permissions.size()]), PERMISSIONS_REQUEST_CODE);
        }
        mIsShowingNativeDialog.set(true);
    }

    private PermissionStates getPermissionStates(Collection<String> pendingPermissions) {
        PermissionStates permissionStates = new PermissionStates();

        for (String permission : pendingPermissions) {
            int permissionState = checkSelfPermission(mActivity, permission);
            switch (permissionState) {
                case PackageManager.PERMISSION_DENIED:
                    permissionStates.addDeniedPermission(permission);
                    break;
                case PackageManager.PERMISSION_GRANTED:
                default:
                    permissionStates.addGrantedPermission(permission);
                    break;
            }
        }

        return permissionStates;
    }

    /*
     * Workaround for RuntimeException of Parcel#readException.
     *
     * For additional details:
     * https://github.com/Karumi/Dexter/issues/86
     */
    private int checkSelfPermission(Activity activity, String permission) {
        try {
            return androidPermissionService.checkSelfPermission(activity, permission);
        } catch (RuntimeException ignored) {
            return PackageManager.PERMISSION_DENIED;
        }
    }

    private final class PermissionStates {
        private final Collection<String> deniedPermissions = new LinkedList<>();
        private final Collection<String> grantedPermissions = new LinkedList<>();

        private void addDeniedPermission(String permission) {
            deniedPermissions.add(permission);
        }

        private void addGrantedPermission(String permission) {
            grantedPermissions.add(permission);
        }

        private Collection<String> getDeniedPermissions() {
            return deniedPermissions;
        }

        private Collection<String> getGrantedPermissions() {
            return grantedPermissions;
        }
    }
}
