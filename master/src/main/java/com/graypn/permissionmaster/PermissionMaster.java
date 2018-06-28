package com.graypn.permissionmaster;

import android.app.Activity;

import com.graypn.permissionmaster.listener.EmptyMultiplePermissionsListener;
import com.graypn.permissionmaster.listener.MultiplePermissionsListener;
import com.graypn.permissionmaster.listener.PermissionListener;

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

    private List<String> mPermissions;
    private MultiplePermissionsListener mMultipleListener = new EmptyMultiplePermissionsListener();

    private PermissionMaster(Activity activity) {
        if (mInstance == null) {
            mInstance = new PermissionMasterInstance(activity);
        } else {
            mInstance.setContext(activity);
        }
    }

    public static PermissionMasterBuilder.Permission withActivity(Activity activity) {
        return new PermissionMaster(activity);
    }

    /** ------- 实现 PermissionMasterBuilder.Permission 相关接口 --------*/
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

    /** ------- 实现 PermissionMasterBuilder.SinglePermissionListener 相关接口 --------*/

    @Override
    public PermissionMasterBuilder withListener(PermissionListener listener) {
        mMultipleListener = new MultiplePermissionsListenerToPermissionListenerAdapter(listener);
        return this;
    }

    /** ------- 实现 PermissionMasterBuilder.MultiPermissionListener 相关接口 --------*/

    @Override
    public PermissionMasterBuilder withListener(MultiplePermissionsListener listener) {
        mMultipleListener = listener;
        return this;
    }

    /** ------- 实现 PermissionMasterBuilder 相关接口 --------*/

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
