package com.example.manager.task.interceptor;

import com.example.manager.database.DownloadEntity;
import com.example.manager.database.download.DaoDownload;
import com.example.manager.database.download.DownloadDaoFatory;
import com.example.manager.task.DownloadInfo;
import com.example.manager.task.DownloadTask;
import com.example.manager.util.FileUtils;
import com.example.manager.util.Logl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghao on 2021/6/1
 */
public class DatabaseInterceptor extends AbstractIntercepter implements TaskInterceptor {
    @Override
    public DownloadTask operate(DownloadTask task) {
        // 文件缓存已不存在
        if (task.getCacheSize() == 0) {
            return task;
        }
        DaoDownload daoDownload = DownloadDaoFatory.getDao();
        long dbSize = daoDownload.qureyAllCacheSize(task.getUrl());
        Logl.e("dbSize: " + dbSize);
        Logl.e("getCacheSize(): " + task.getCacheSize());
        if (task.getCacheSize() < dbSize) {
            forceDelete(task);
            return task;
        }
        // 判断文件长度是否变化，在此若长度没变视为同一个文件下载连接。
        List<DownloadInfo> list = new ArrayList<>();
        task.setInfoList(list);
        List<DownloadEntity> entityList = daoDownload.qureyAllByUrl(task.getUrl());
        int totalDbSize = 0;
        for (DownloadEntity entity : entityList) {
            Logl.e(entity.toString());
            totalDbSize += entity.getContentLength();
            if (entity.getProgress() == entity.getContentLength()) {
                continue;
            }
            Logl.e("添加缓存开始");
            list.add(new DownloadInfo(entity.getId(), entity.getStart() + entity.getProgress(), entity.getContentLength()));
        }
        Logl.e("totalDbSize:" + totalDbSize);
        Logl.e("task.getTotalSize():" + task.getTotalSize());
        if (totalDbSize != task.getTotalSize()) {
            forceDelete(task);
            task.setInfoList(null);
        }
        return task;
    }

    private void forceDelete(DownloadTask task) {
        task.setForceRepeat(true);
        task.setCacheSize(0);
        FileUtils.delete(FileUtils.getTargetFilePath(task.getFileParent(), task.getFileName()));
        FileUtils.createNewFile(FileUtils.getTargetFilePath(task.getFileParent(), task.getFileName()));
        DownloadDaoFatory.getDao().deleteByUrl(task.getUrl());
    }

}