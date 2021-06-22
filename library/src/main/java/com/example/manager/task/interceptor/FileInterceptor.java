package com.example.manager.task.interceptor;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.example.manager.constant.Status;
import com.example.manager.database.DownloadProvider;
import com.example.manager.permission.MyPermissionActivity;
import com.example.manager.permission.core.IPermission;
import com.example.manager.permission.util.PermissionHelper;
import com.example.manager.permission.util.PermissionUtils;
import com.example.manager.task.DownloadTask;
import com.example.manager.util.DownloadUtils;
import com.example.manager.util.FileUtils;
import com.example.manager.util.Logl;

import java.io.File;
import java.io.IOException;

/**
 * Created by tanghao on 2021/5/27
 */
public class FileInterceptor extends AbstractIntercepter implements TaskInterceptor {

    @Override
    public DownloadTask operate(DownloadTask task) {
        if (task.getFileName() == null) {
            task.setFileName(DownloadUtils.getFileNameFromUrl(task.getUrl()));
        }
        if (TextUtils.isEmpty(task.getFileParent())) {
            task.setFileParent(FileUtils.getDefaultSaveRootPath());
        }
        if (!dealFilePermission(task)) {
            task.cancel();
            task.setStatus(Status.ERRO);
            return task;
        }
        // 判断文件是否存在
        // 如果存在，判断是否是已需要的文件
        // 文件与预期不符或者不知道符不符,都不管
        File file = new File(FileUtils.getTargetFilePath(task.getFileParent(), task.getFileName()));
        if (file.exists()) {
            if (file.length() != 0 && !TextUtils.isEmpty(task.getNewFileMd5())) {
                if (FileUtils.fileMd5(file.getAbsolutePath()).equals(task.getNewFileMd5())) {
                    task.setStatus(Status.DOWN);
                }
            }
            task.setCacheSize(file.length());
            if (task.forceRepeat()) {
                task.forceDelete();
            }
        }
        return task;
    }

    /**
     * 处理权限相关，如果不符合，将取消本次下载
     *
     * @return
     */
    private boolean dealFilePermission(DownloadTask task) {
        Logl.e("DownloadProvider.context.getPackageName(): " + DownloadProvider.context.getPackageName());
        String defaultPath = "";
        if (DownloadProvider.context.getCacheDir() != null) {
            defaultPath = DownloadProvider.context.getCacheDir().getParent();
        } else {
            defaultPath = DownloadProvider.context.getPackageName();
        }
        if (defaultPath == null) {
            defaultPath = "data/user";
        }
        if (task.getFileParent().contains(defaultPath)) {
            return true;
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return true;
        } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
            if (PermissionUtils.hasFilePermission()) {
                return true;
            } else {
                task.getStatus().setMsg(Status.PERMISSION_REQUESTING);
                PermissionHelper.requestStoragePermision(new IPermission() {
                    @Override
                    public void ganted() {
                        task.restart();
                    }

                    @Override
                    public void cancel() {
                    }

                    @Override
                    public void denied() {
                    }
                });
                return false;
            }
        } else {
            if (Environment.isExternalStorageManager()) {
                return true;
            }else{
                PermissionHelper.requestStorageManagerPermission(new IPermission() {
                    @Override
                    public void ganted() {
                        task.restart();
                    }
                    @Override
                    public void cancel() {
                    }
                    @Override
                    public void denied() {
                    }
                });
                task.getStatus().setMsg(Status.PERMISSION_REQUESTING);
            }
        }
        return true;
    }

}