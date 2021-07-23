package com.thjolin.download.task.interceptor;

import com.thjolin.download.database.DownloadEntity;
import com.thjolin.download.database.download.DownloadDaoFatory;
import com.thjolin.download.task.DownloadInfo;
import com.thjolin.download.task.DownloadTask;
import com.thjolin.util.Logl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghao on 2021/6/1
 */
public class StrategyInterceptor extends AbstractIntercepter implements TaskInterceptor {

    // 1 connection: [0, 2MB)
    private static final long ONE_CORE_SIZE_LIMIT = 2 * 1024 * 1024; // 2MiB
    // 2 connection: [2MB, 10MB)
    private static final long TWO_CORE_SIZE_LIMIT = 10 * 1024 * 1024; // 10MiB
    // 3 connection: [5MB, 50MB)
    private static final long THREE_CORE_SIZE_LIMIT = 50 * 1024 * 1024; // 50MiB
    // 4 connection: [50MB, 100MB)
    private static final long FOUR_CORE_SIZE_LIMIT = 100 * 1024 * 1024; // 100MiB
    // 5 connection: [100MB,+)

    @Override
    public DownloadTask operate(DownloadTask task) {
        if (task.getInfoList() != null) {
            return task;
        }
        countCoreBlockSize(task);
        createDownTask(task);
        if (task.isNeedProgress() && task.getDownloadListener() != null) {
            task.createProgressController();
        }
        return task;
    }

    private void countCoreBlockSize(DownloadTask task) {
        if (task.getBlockSize() != 0) {
            return;
        }
        if (task.getTotalSize() < ONE_CORE_SIZE_LIMIT) {
            task.setBlockSize(1);
        } else if (task.getTotalSize() < TWO_CORE_SIZE_LIMIT) {
            task.setBlockSize(2);
        } else if (task.getTotalSize() < THREE_CORE_SIZE_LIMIT) {
            task.setBlockSize(3);
        } else if (task.getTotalSize() < FOUR_CORE_SIZE_LIMIT) {
            task.setBlockSize(4);
        } else {
            task.setBlockSize(5);
        }
    }

    private void createDownTask(DownloadTask task) {
        final long eachLength = task.getTotalSize() / task.getBlockSize();
        long startOffset = 0;
        long contentLength = 0;
        List<DownloadInfo> list = new ArrayList<>();
        task.setInfoList(list);
        List<DownloadEntity> listEntity = new ArrayList<>();
        for (int i = 0; i < task.getBlockSize(); i++) {
            startOffset = startOffset + contentLength;
            if (i == 0) {
                // first block do more, because he start first
                final long remainLength = task.getTotalSize() % task.getBlockSize();
                contentLength = eachLength + remainLength;
            } else {
                contentLength = eachLength;
            }
            list.add(new DownloadInfo(startOffset, contentLength));
            listEntity.add(new DownloadEntity(startOffset, task.getUrl(), i, 0, contentLength));
        }
        DownloadDaoFatory.getDao().insert(listEntity);
        for (int i = 0; i < list.size(); i++) {
            Logl.e("是否有Id");
            list.get(i).setId(listEntity.get(i).getId());
        }
    }
}