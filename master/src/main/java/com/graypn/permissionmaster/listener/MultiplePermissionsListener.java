package com.graypn.permissionmaster.listener;

import com.graypn.permissionmaster.model.MultiplePermissionsReport;

/**
 * Interface that listens to updates to the permission requests
 */
public interface MultiplePermissionsListener {

  /**
   * Method called when all permissions has been completely checked
   *
   * @param report In detail report with all the permissions that has been denied and granted
   */
  void onPermissionsChecked(MultiplePermissionsReport report);

}
