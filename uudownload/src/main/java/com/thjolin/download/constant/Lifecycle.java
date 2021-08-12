package com.thjolin.download.constant;

import com.thjolin.download.listener.DownloadListener;
import com.thjolin.download.task.DownloadTask;

/**
 * Created by tanghao on 2021/5/27
 */
public interface Lifecycle {

    void init();

    boolean prepare(DownloadTask task);

    void start(DownloadTask task, DownloadListener downloadListener);

    void destroy();

}
