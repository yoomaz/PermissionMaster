package com.graypn.permissionmaster;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.graypn.permissionmaster.listener.EmptyMultiplePermissionsListener;
import com.graypn.permissionmaster.listener.MultiplePermissionsListener;
import com.graypn.permissionmaster.listener.SinglePermissionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by ZhuLei on 2017/3/6.
 * Email: zhuleineuq@gmail.com
 */

public class PermissionMaster implements
        PermissionMasterBuilder,
        PermissionMasterBuilder.PermissionBuilder,
        PermissionMasterBuilder.SinglePermissionListenerBuilder,
        PermissionMasterBuilder.MultiPermissionListenerBuilder {

    private static PermissionMasterInstance mInstance;

    private List<String> mPermissions;
    private MultiplePermissionsListener mMultipleListener = new EmptyMultiplePermissionsListener();

    private PermissionMaster(Activity activity) {
        if (mInstance == null) {
            mInstance = new PermissionMasterInstance(activity);
        } else {
            mInstance.setContext(activity);
        }
    }

    public static PermissionMasterBuilder.PermissionBuilder withActivity(Activity activity) {
        return new PermissionMaster(activity);
    }

    /**
     * 检查权限
     */
    public static boolean checkPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            int permissionState = ContextCompat.checkSelfPermission(context, permission);
            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 打开系统设置界面
     * <p>
     * 利用系统 Dialog 实现默认方式，可以根据 app 自己的 UI 去实现自定义
     */
    public static void openSettingDialog(final Activity context, int requestCode, final DialogInterface.OnClickListener onCancelClickListener) {
        PermissionMasterInstance.openSettingDialog(context, requestCode, null, null, onCancelClickListener);
    }

    /**
     * 打开系统设置界面
     * <p>
     * 利用系统 Dialog 实现默认方式，可以根据 app 自己的 UI 去实现自定义
     */
    public static void openSettingDialog(final Activity context, int requestCode, String title, String content, final DialogInterface.OnClickListener onCancelClickListener) {
        PermissionMasterInstance.openSettingDialog(context, requestCode, title, content, onCancelClickListener);
    }

    /**
     * ------- 实现 PermissionMasterBuilder.Permission 相关接口 --------
     */
    @Override
    public SinglePermissionListenerBuilder withPermission(String permission) {
        mPermissions = Collections.singletonList(permission);
        return this;
    }

    @Override
    public MultiPermissionListenerBuilder withPermissions(String... permissions) {
        mPermissions = Arrays.asList(permissions);
        return this;
    }

    @Override
    public MultiPermissionListenerBuilder withPermissions(Collection<String> permissions) {
        mPermissions = new ArrayList<>(permissions);
        return this;
    }

    /**
     * ------- 实现 PermissionMasterBuilder.SinglePermissionListener 相关接口 --------
     */

    @Override
    public PermissionMasterBuilder withListener(SinglePermissionListener listener) {
        mMultipleListener = new MultiplePermissionsListenerToPermissionListenerAdapter(listener);
        return this;
    }

    /**
     * ------- 实现 PermissionMasterBuilder.MultiPermissionListener 相关接口 --------
     */

    @Override
    public PermissionMasterBuilder withListener(MultiplePermissionsListener listener) {
        mMultipleListener = listener;
        return this;
    }

    /**
     * ------- 实现 PermissionMasterBuilder 相关接口 --------
     */

    @Override
    public void check() {
        try {
            mInstance.checkPermissions(mMultipleListener, mPermissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向操作系统申请权限的 Activity 就绪
     */
    static void onActivityReady(Activity activity) {
        if (mInstance != null) {
            mInstance.onActivityReady(activity);
        }
    }

    /**
     * 处理操作系统权限申请结果
     */
    static void onPermissionsRequested(List<String> grantedPermissions, List<String> deniedPermissions) {
        if (mInstance != null) {
            mInstance.updatePermissionsAsGranted(grantedPermissions);
            mInstance.updatePermissionsAsDenied(deniedPermissions);
        }
    }
}
