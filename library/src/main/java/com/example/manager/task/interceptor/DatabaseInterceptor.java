package com.example.manager.task.interceptor;

import com.example.manager.task.DownloadTask;
import com.example.manager.util.FileUtils;

import java.io.File;

/**
 * Created by tanghao on 2021/6/1
 */
public class DatabaseInterceptor extends AbstractIntercepter implements TaskInterceptor {
    @Override
    public DownloadTask operate(DownloadTask task) {
        long dbSize = 0; // TODO db cache.
        if (task.getCacheSize() != 0 && task.getTotalSize() != task.getCacheSize() + dbSize) {
            task.setForceRepeat(true);
            task.setCacheSize(0);
            FileUtils.delete(FileUtils.getTargetFilePath(task.getFileParent(), task.getFileName()));
            FileUtils.createNewFile(task.getFileParent(), task.getFileName());
        }
        return task;
    }
}