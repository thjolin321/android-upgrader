package com.example.manager.permission.util;

import com.example.manager.database.DownloadProvider;
import com.example.manager.permission.MyPermissionActivity;
import com.example.manager.permission.core.IPermission;
import com.example.manager.util.Logl;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by tanghao on 2021/6/21
 */
public class PermissionHelper {

    public static void requestStoragePermision(IPermission iPermission) {
        MyPermissionActivity.requestPermissionAction(DownloadProvider.context,
                new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}
                , 0x555, iPermission);
    }

    public static void requestStorageManagerPermission(IPermission iPermission) {
        MyPermissionActivity.requestStorageManagerPermission(DownloadProvider.context,
                iPermission);
    }

}