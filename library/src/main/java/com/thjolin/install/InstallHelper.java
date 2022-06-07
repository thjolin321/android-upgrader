package com.thjolin.install;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;


import com.thjolin.download.database.DownloadProvider;
import com.thjolin.download.permission.MyPermissionActivity;
import com.thjolin.download.util.Utils;
import com.thjolin.download.util.Logl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by th on 2021/6/15
 */
public class InstallHelper {

    public static void installWithActivity(String apkPath) {
        Intent intent = new Intent(DownloadProvider.context, InstallApkActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("apkPath", apkPath);
        DownloadProvider.context.startActivity(intent);
    }

    public static void showDialogActivity(boolean showNotification, boolean forceUpdate,
                                          boolean needDownload, boolean needCompose, String apkPath
            , String fileName) {
        Intent intent = new Intent(DownloadProvider.context, InstallApkActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("forceUpdate", forceUpdate);
        intent.putExtra("needDownload", needDownload);
        intent.putExtra("needCompose", needCompose);
        intent.putExtra("showNotification", showNotification);
        intent.putExtra("fileName", fileName);
        intent.putExtra("needUi", true);
        intent.putExtra("apkPath", apkPath);
        DownloadProvider.context.startActivity(intent);
    }

    public static void gotoMarket() {
        // TODO 控制跳转应用市场
    }

    public static void installApkWithIntent(FragmentActivity activity, File apkFile) {
        //文件有所有者概念，现在是属于当前进程的，需要把这个文件暴露给系统安装程序（其他进程）去安装
        //因此，可能会存在权限问题，需要做下面的设置
        //如果文件是sdcard上的，就不需要这个操作了
        try {
            apkFile.setExecutable(true, false);
            apkFile.setReadable(true, false);
            apkFile.setWritable(true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;

        //TODO N FileProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(apkFile);
        }

        intent.setDataAndType(uri,"application/vnd.android.package-archive");
        activity.startActivity(intent);
        activity.finish();
        //TODO 0 INSTALL PERMISSION
        //在AndroidManifest中加入权限即可
    }

}