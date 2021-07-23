package com.thjolin.install;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;


import com.thjolin.download.database.DownloadProvider;
import com.thjolin.download.permission.MyPermissionActivity;
import com.thjolin.util.Utils;
import com.thjolin.util.Logl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by tanghao on 2021/6/15
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

    public static void install(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkPath),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void installApk(Context context, String apkPath) {
        Logl.e("installApk: " + apkPath);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        PackageInstaller packageInstaller = context
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
            outputStream = session.openWrite("tdashi1", 0, -1);
            fileInputStream = new FileInputStream(apkPath);
            byte[] buffer = new byte[10 * 1024 * 1024];
            int len;
            while ((len = fileInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
//            fileInputStream.close();
//            Logl.e("关out");
//            outputStream.flush();
//            outputStream.close();
            session.commit(createIntentSender(context));
        } catch (Exception e) {
            Logl.e("安装报错：" + e.getMessage());
            e.printStackTrace();
        } finally {
            Utils.close(fileInputStream);
            Utils.close(outputStream);
        }
    }

    private static IntentSender createIntentSender(Context context) {
        Intent intent = new Intent(context, MyPermissionActivity.class);
        intent.setAction("com.uu.install");
        return PendingIntent.getActivity(context,
                0, intent, 0).getIntentSender();
    }


    public static void installApkFinal(Context context, String path) {
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Logl.e("packageName: " + context.getPackageName());
            Uri contentUri = FileProvider.getUriForFile(context, "com.example.updatefrompatch.fileprovider", file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        Logl.e("开始安装 startActivity");
        context.startActivity(intent);
        Logl.e("开始安装 startActivity 11");
    }

    public static void gotoMarket() {
    }

    public static void installWithNoActivity(Context activity, final String filePath) {
        Logl.e("installWithNoActivity");
        PackageInstaller.Session session = null;
        if (Build.VERSION.SDK_INT < 21) {
            //as PackageInstaller was added in API 21, let's use the old way of doing it prior to 21
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            Uri apkUri = Uri.parse(filePath);
            Context context = activity.getApplicationContext();
            ApplicationInfo appInfo = context.getApplicationInfo();
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, false);
            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME,
                    appInfo.packageName);
            activity.startActivity(intent);
        } else {
            try {
                PackageInstaller packageInstaller = null;
                packageInstaller = DownloadProvider.context.getPackageManager().getPackageInstaller();
                PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                        PackageInstaller.SessionParams.MODE_FULL_INSTALL);
                int sessionId = packageInstaller.createSession(params);
                session = packageInstaller.openSession(sessionId);
                addApkToInstallSession(session, filePath);
                Intent intent = new Intent(activity, InstallApkActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);
                IntentSender statusReceiver = pendingIntent.getIntentSender();
                session.commit(statusReceiver);
            } catch (
                    IOException e) {
                throw new RuntimeException("Couldn't install package", e);
            } catch (
                    RuntimeException e) {
                if (session != null) {
                    session.abandon();
                }
                throw e;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void addApkToInstallSession(PackageInstaller.Session session, String apkPath)
            throws IOException {
        try (OutputStream packageInSession = session.openWrite("package", 0, -1);
             InputStream is = new FileInputStream(apkPath)) {
            byte[] buffer = new byte[16384];
            int n;
            while ((n = is.read(buffer)) >= 0) {
                packageInSession.write(buffer, 0, n);
            }
        }

    }


}