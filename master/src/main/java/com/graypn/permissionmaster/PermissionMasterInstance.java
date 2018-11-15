package com.graypn.permissionmaster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import com.graypn.permissionmaster.listener.EmptyMultiplePermissionsListener;
import com.graypn.permissionmaster.model.MultiplePermissionsReport;
import com.graypn.permissionmaster.model.PermissionResponse;
import com.graypn.permissionmaster.listener.MultiplePermissionsListener;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 权限请求实体
 */

final class PermissionMasterInstance {

    private static final int PERMISSIONS_REQUEST_CODE = 42;
    private static final MultiplePermissionsListener EMPTY_LISTENER = new EmptyMultiplePermissionsListener();

    private WeakReference<Context> mContext;
    private Activity mActivity;
    private AndroidPermissionService androidPermissionService;
    private final Collection<String> mPendingPermissions;
    private final MultiplePermissionsReport mMultiplePermissionsReport;
    private MultiplePermissionsListener mMultiplePermissionsListener;
    // 原子操作，确定当前只进行一个系统权限申请请求
    private final AtomicBoolean mIsShowingNativeDialog;

    PermissionMasterInstance(Context context) {
        setContext(context);

        androidPermissionService = new AndroidPermissionService();
        mPendingPermissions = new TreeSet<>();
        mMultiplePermissionsReport = new MultiplePermissionsReport();
        mIsShowingNativeDialog = new AtomicBoolean();
    }

    /**
     * 打开系统设置界面
     */
    static void openSettingDialog(final Activity context, final int requestCode, String title, String content, final DialogInterface.OnClickListener onCancelClickListener) {
        String dialogTitle = title;
        String dialogContent = content;
        if (TextUtils.isEmpty(dialogTitle)) {
            dialogTitle = context.getResources().getString(R.string.permissionmaster_setting_dialog_title);
        }
        if (TextUtils.isEmpty(dialogContent)) {
            dialogContent = context.getResources().getString(R.string.permissionmaster_setting_dialog_content);
        }
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(dialogTitle)
                .setMessage(dialogContent)
                .setPositiveButton(context.getResources().getString(R.string.permissionmaster_setting_dialog_btn_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivityForResult(intent, requestCode);
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.permissionmaster_setting_dialog_btn_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (onCancelClickListener != null) {
                            onCancelClickListener.onClick(dialog, which);
                        }
                    }
                }).show();
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
                report.addGrantedPermissionResponse(PermissionResponse.from(permission));
            }
            listener.onPermissionsChecked(report);
        } else {
            // 开始去申请权限
            startTransparentActivity();
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
            PermissionResponse response = PermissionResponse.from(permission);
            mMultiplePermissionsReport.addGrantedPermissionResponse(response);
        }
        onPermissionsChecked(permissions);
    }

    void updatePermissionsAsDenied(Collection<String> permissions) {
        for (String permission : permissions) {
            PermissionResponse response = PermissionResponse.from(permission,
                    !androidPermissionService.shouldShowRequestPermissionRationale(mActivity, permission));
            mMultiplePermissionsReport.addDeniedPermissionResponse(response);
        }
        onPermissionsChecked(permissions);
    }

    /**
     * 检查所有权限是否都已经拥有
     */
    private boolean isEveryPermissionGranted(Collection<String> permissions, Context context) {
        for (String permission : permissions) {
            int permissionState = androidPermissionService.checkSelfPermission(context, permission);
            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startTransparentActivity() {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, PermissionMasterActivity.class);
        context.startActivity(intent);
    }

    /**
     * 更新待校验的权限状态
     */
    private void onPermissionsChecked(Collection<String> permissions) {
        if (mPendingPermissions.isEmpty()) {
            return;
        }
        mPendingPermissions.removeAll(permissions);
        if (mPendingPermissions.isEmpty()) {
            mActivity.finish();
            mActivity = null;
            // 重置向系统申请权限的开关
            mIsShowingNativeDialog.set(false);
            MultiplePermissionsListener currentListener = mMultiplePermissionsListener;
            // 不保留 Listener 引用，防止内存泄漏
            mMultiplePermissionsListener = EMPTY_LISTENER;
            currentListener.onPermissionsChecked(mMultiplePermissionsReport);
        }
    }

    /**
     * 向操作系统申请权限
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
