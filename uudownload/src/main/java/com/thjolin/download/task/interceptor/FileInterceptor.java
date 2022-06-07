package com.thjolin.download.task.interceptor;

import android.os.Environment;
import android.text.TextUtils;

import com.thjolin.download.constant.Status;
import com.thjolin.download.database.DownloadProvider;
import com.thjolin.download.permission.core.IPermissionImpl;
import com.thjolin.download.permission.util.PermissionHelper;
import com.thjolin.download.permission.util.PermissionUtils;
import com.thjolin.download.task.DownloadTask;
import com.thjolin.download.util.FileHelper;
import com.thjolin.download.util.Logl;
import com.thjolin.download.util.Utils;

import java.io.File;

/**
 * Created by th on 2021/5/27
 */
public class FileInterceptor extends AbstractInterceptor implements TaskInterceptor {

    @Override
    public DownloadTask operate(DownloadTask task) {
        if (task.getFileName() == null) {
            task.setFileName(Utils.getFileNameFromUrl(task.getUrl()));
        }
        if (TextUtils.isEmpty(task.getFileParent())) {
            task.setFileParent(FileHelper.getDefaultSaveRootPath());
        } else {
            FileHelper.creatDirectory(task.getFileParent());
        }

        if (!dealFilePermission(task)) {
            return task;
        }
        // 判断文件是否存在
        // 如果存在，判断是否是已需要的文件
        // 文件与预期不符或者不知道符不符,都不管
        File file = new File(FileHelper.getTargetFilePath(task.getFileParent(), task.getFileName()));
        if (file.exists()) {
            if (file.length() != 0 && !TextUtils.isEmpty(task.getNewFileMd5())) {
                if (FileHelper.fileMd5(file.getAbsolutePath()).equals(task.getNewFileMd5())) {
                    task.setStatus(Status.DOWN);
                }
            }
            task.setCacheSize(FileHelper.getFileSize(file));
            if (task.forceRepeat() && task.getStatus().getCode() != Status.DOWN) {
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
                task.setStatus(Status.ERRO);
                PermissionHelper.requestStoragePermision(new IPermissionImpl() {
                    @Override
                    public void ganted() {
                        task.restart();
                    }
                });
                return false;
            }
        } else {
            if (Environment.isExternalStorageManager()) {
                return true;
            } else {
                task.getStatus().setMsg(Status.PERMISSION_REQUESTING);
                task.setStatus(Status.ERRO);
                PermissionHelper.requestStorageManagerPermission(new IPermissionImpl() {
                    @Override
                    public void ganted() {
                        task.restart();
                    }
                });
                task.getStatus().setMsg(Status.PERMISSION_REQUESTING);
            }
        }
        return true;
    }

}