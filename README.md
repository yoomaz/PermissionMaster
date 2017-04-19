# PermissionMaster
一个 Android 权限请求框架，简化请求的逻辑，一行代码完成权限请求的操作

## Usage

1. 单个权限请求，传入 `PermissionListener` 回调处理请求的结果，如果用户拒绝了，可以调用 `PermissionDeniedResponse.isPermanentlyDenied()` 确定是否是永久拒绝，如果是永久拒绝了，一般会进一步提示用户需要开启权限，然后跳转到应用设置界面让用户手动开启。

   ```java
   PermissionMaster.withActivity(MainActivity.this)
           .withPermission(Manifest.permission.CAMERA)
           .withListener(new PermissionListener() {
               @Override
               public void onPermissionGranted(PermissionGrantedResponse response) {
                   Log.i("MainActivity", "onPermissionGranted：" + response.getPermissionName());
               }
               @Override
               public void onPermissionDenied(PermissionDeniedResponse response) {
                   Log.i("MainActivity", "onPermissionDenied:" + response.isPermanentlyDenied());
               }
           })
           .check();
   ```

   ​

2. 一次多个权限请求，传入 `MultiplePermissionsListener` 回调处理请求的结果

   ```java
   PermissionMaster.withActivity(MainActivity.this)
           .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO)
           .withListener(new MultiplePermissionsListener() {
               @Override
               public void onPermissionsChecked(MultiplePermissionsReport report) {
                   List<PermissionGrantedResponse> grantedPermissionResponses = report.getGrantedPermissionResponses();
                   List<PermissionDeniedResponse> deniedPermissionResponses = report.getDeniedPermissionResponses();
                   for (PermissionGrantedResponse response : grantedPermissionResponses) {
                       Log.i("MainActivity", "onPermissionGranted：" + response.getPermissionName());
                   }
                   for (PermissionDeniedResponse response : deniedPermissionResponses) {
                       Log.i("MainActivity", "onPermissionDenied:" + response.getPermissionName() + ":" + response.isPermanentlyDenied());
                   }
               }
           })
           .check();
   ```

   ​