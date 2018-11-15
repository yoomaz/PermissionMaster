package com.graypn.permissionmaster.listener;

import com.graypn.permissionmaster.model.MultiplePermissionsReport;

/**
 * 默认空响应回调，当用户没有设置回调的时候使用
 */
public class EmptyMultiplePermissionsListener implements MultiplePermissionsListener {

  @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

  }

}
