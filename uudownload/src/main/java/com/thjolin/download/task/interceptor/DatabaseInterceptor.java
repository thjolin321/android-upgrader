package com.thjolin.download.task.interceptor;

import com.thjolin.download.database.DownloadEntity;
import com.thjolin.download.database.download.DaoDownload;
import com.thjolin.download.database.download.DownloadDaoFatory;
import com.thjolin.download.task.DownloadTask;
import com.thjolin.download.util.Logl;

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
            task.forceDelete();
            return task;
        }
        task.setCacheSize(dbSize);
        // 判断文件长度是否变化，在此若长度没变视为同一个文件下载连接。
        List<DownloadEntity> entityList = daoDownload.qureyAllByUrl(task.getUrl());
        task.setInfoList(entityList);
        long totalDbSize = 0;
        for (DownloadEntity entity : entityList) {
            Logl.e(entity.toString());
            totalDbSize += entity.getContentLength();
        }
        Logl.e("totalDbSize:" + totalDbSize);
        Logl.e("task.getTotalSize():" + task.getTotalSize());
        if (totalDbSize != task.getTotalSize()) {
            task.forceDelete();
            task.setInfoList(null);
        }
        return task;
    }
}