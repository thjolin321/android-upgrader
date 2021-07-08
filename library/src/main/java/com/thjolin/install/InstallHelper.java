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


    private static final String TAG = "TAG";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void JIntentActionInstallApk(Context activity, final String filename) {
        PackageInstaller.Session session = null;
        try {
            Log.e(TAG, "JIntentActionInstallApk " + filename);

            if (Build.VERSION.SDK_INT < 21) {
                //as PackageInstaller was added in API 21, let's use the old way of doing it prior to 21
                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                Uri apkUri = Uri.parse(filename);
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
                // API level 21 or higher, we need to use PackageInstaller
                PackageInstaller packageInstaller = activity.getPackageManager().getPackageInstaller();
                Log.e(TAG, "JIntentActionInstallApk - got packageInstaller");
                PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                        PackageInstaller.SessionParams.MODE_FULL_INSTALL);
                Log.e(TAG, "JIntentActionInstallApk - set SessionParams");
                int sessionId = packageInstaller.createSession(params);
                session = packageInstaller.openSession(sessionId);
                Log.e(TAG, "JIntentActionInstallApk - session opened");

                // Create an install status receiver.
                Context context = activity.getApplicationContext();
                addApkToInstallSession(context, filename, session);
                Log.e(TAG, "JIntentActionInstallApk - apk added to session");

                Intent intent = new Intent(context, InstallActivity.class);
                intent.setAction(InstallActivity.ACTION_INSTALL);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                IntentSender statusReceiver = pendingIntent.getIntentSender();
                // Commit the session (this will start the installation workflow).
                session.commit(statusReceiver);
                Log.e(TAG, "JIntentActionInstallApk - commited");
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't install package", e);
        } catch (RuntimeException e) {
            if (session != null) {
                session.abandon();
            }
            throw e;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void addApkToInstallSession(Context context, String filename, PackageInstaller.Session session) {
        Log.e(TAG, "addApkToInstallSession " + filename);
        // It's recommended to pass the file size to openWrite(). Otherwise installation may fail
        // if the disk is almost full.
        try {
            OutputStream packageInSession = session.openWrite("package", 0, -1);
            InputStream input;
            Uri uri = Uri.parse(filename);
            input = context.getContentResolver().openInputStream(uri);

            if (input != null) {
                Log.e(TAG, "input.available: " + input.available());
                byte[] buffer = new byte[16384];
                int n;
                while ((n = input.read(buffer)) >= 0) {
                    packageInSession.write(buffer, 0, n);
                }
            } else {
                Log.e(TAG, "addApkToInstallSession failed");
                throw new IOException("addApkToInstallSession");
            }
            packageInSession.close();  //need to close this stream
            input.close();             //need to close this stream
        } catch (Exception e) {
            Log.e(TAG, "addApkToInstallSession failed2 " + e.toString());
        }
    }


}