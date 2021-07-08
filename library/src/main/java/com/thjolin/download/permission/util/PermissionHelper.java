package com.thjolin.download.permission.util;

import com.thjolin.download.database.DownloadProvider;
import com.thjolin.download.permission.MyPermissionActivity;
import com.thjolin.download.permission.core.IPermission;

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