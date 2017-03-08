package com.graypn.permissionmaster.listener;

import com.graypn.permissionmaster.model.MultiplePermissionsReport;

/**
 * Empty implementation of {@link MultiplePermissionsListener} to allow extensions to implement
 * only the required methods
 */
public class EmptyMultiplePermissionsListener implements MultiplePermissionsListener {

  @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

  }

}
