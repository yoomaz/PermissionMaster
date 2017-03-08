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
import com.graypn.permissionmaster.model.PermissionDeniedResponse;
import com.graypn.permissionmaster.model.PermissionGrantedResponse;
import com.graypn.permissionmaster.listener.PermissionListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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



//                PermissionMaster.withActivity(MainActivity.this)
//                        .withPermission(Manifest.permission.CAMERA)
//                        .withListener(new PermissionListener() {
//                            @Override
//                            public void onPermissionGranted(PermissionGrantedResponse response) {
//                                Log.i("MainActivity", "onPermissionGranted：" + response.getPermissionName());
//                            }
//
//                            @Override
//                            public void onPermissionDenied(PermissionDeniedResponse response) {
//                                Log.i("MainActivity", "onPermissionDenied:" + response.isPermanentlyDenied());
//                            }
//                        })
//                        .check();
            }
        });
    }
}
