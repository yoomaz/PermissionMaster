package com.graypn.permissionmaster.sample;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.graypn.permissionmaster.PermissionMaster;
import com.graypn.permissionmaster.listener.MultiplePermissionsListener;
import com.graypn.permissionmaster.model.MultiplePermissionsReport;
import com.graypn.permissionmaster.model.PermissionResponse;
import com.graypn.permissionmaster.listener.SinglePermissionListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnSingle;
    private Button btnMulti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSingle = findViewById(R.id.btn_single);
        btnMulti = findViewById(R.id.btn_multi);

        btnSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionMaster.checkPermissions(MainActivity.this, Manifest.permission.CAMERA)) {
                    return;
                }

                PermissionMaster.withActivity(MainActivity.this)
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new SinglePermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionResponse response) {
                                Log.i("MainActivity", "onPermissionGranted：" + response.getPermissionName());
                            }

                            @Override
                            public void onPermissionDenied(PermissionResponse response) {
                                Log.i("MainActivity", "onPermissionDenied:" + response.isPermanentlyDenied());
                            }
                        })
                        .check();
            }
        });

        btnMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionMaster.checkPermissions(MainActivity.this, Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO)) {
                    return;
                }

                PermissionMaster.withActivity(MainActivity.this)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // 是否所有权限都被授予
                                boolean allPermissionsGranted = report.areAllPermissionsGranted();
                                // 有权限被拒绝
                                boolean isAnyPermissionPermanentlyDenied = report.isAnyPermissionPermanentlyDenied();
                                // 获取被允许的权限列表
                                List<PermissionResponse> grantedPermissionResponses = report.getGrantedPermissionResponses();
                                // 获取被拒绝的权限列表
                                List<PermissionResponse> deniedPermissionResponses = report.getDeniedPermissionResponses();
                            }
                        })
                        .check();
            }
        });
    }
}
