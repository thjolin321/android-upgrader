package com.example.install;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.net.Uri;

import com.example.manager.database.DownloadProvider;
import com.example.manager.permission.MyPermissionActivity;
import com.example.manager.util.DownloadUtils;
import com.example.manager.util.Logl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by tanghao on 2021/6/15
 */
public class InstallHelper {

    public static void install(String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkPath),
                "application/vnd.android.package-archive");

        DownloadProvider.context.startActivity(intent);
    }

    public static void installApk(String apkPath) {
        Logl.e("installApk");
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        PackageInstaller packageInstaller = DownloadProvider.context
                .getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        //创建ID
        int id = 0;
        PackageInstaller.Session session = null;
        OutputStream outputStream = null;
        FileInputStream fileInputStream = null;
        try {
            id = packageInstaller.createSession(params);
            session = packageInstaller.openSession(id);
            outputStream = session.openWrite("apk", 0, -1);
            fileInputStream = new FileInputStream(apkPath);
            byte[] buffer = new byte[10 * 1024 * 1024];
            int len;
            while ((len = fileInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            session.commit(createIntentSender());
        } catch (Exception e) {
            Logl.e("安装报错："+e.getMessage());
            e.printStackTrace();
        } finally {
            DownloadUtils.close(fileInputStream);
            DownloadUtils.close(outputStream);
        }
    }

    private static IntentSender createIntentSender() {
        Intent intent = new Intent(DownloadProvider.context, MyPermissionActivity.class);
        intent.setAction("com.uu.install");
        return PendingIntent.getActivity(DownloadProvider.context,
                0, intent, 0).getIntentSender();
    }


    public static void gotoMarket() {
    }

}