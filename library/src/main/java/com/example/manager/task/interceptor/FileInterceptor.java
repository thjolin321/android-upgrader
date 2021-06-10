package com.example.manager.task.interceptor;

import android.text.TextUtils;

import com.example.manager.constant.Status;
import com.example.manager.task.DownloadTask;
import com.example.manager.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by tanghao on 2021/5/27
 */
public class FileInterceptor extends AbstractIntercepter implements TaskInterceptor {

    @Override
    public DownloadTask operate(DownloadTask task) {
        if (task.getFileName() == null) {
            task.setFileName(FileUtils.strMd5(task.getUrl()));
        }
        if (TextUtils.isEmpty(task.getFileParent())) {
            task.setFileParent(FileUtils.getDefaultSaveRootPath());
        }
        File file = new File(FileUtils.getTargetFilePath(task.getFileParent(), task.getFileName()));
        if (file.exists()) {
            if (file.length() != 0 && !TextUtils.isEmpty(task.getNewFileMd5())) {
                if (!FileUtils.fileMd5(file.getAbsolutePath()).equals(task.getNewFileMd5())) {
                    file.delete();
                    task.setForceRepeat(true);
                } else {
                    task.setStatus(Status.DOWN);
                }
            }
            task.setCacheSize(file.length());
            if (task.forceRepeat()) {
                if (file.exists()) {
                    file.delete();
                    task.setCacheSize(0);
                }
            }
        }
        return task;
    }

}