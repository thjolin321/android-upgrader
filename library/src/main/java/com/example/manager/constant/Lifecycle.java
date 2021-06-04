package com.example.manager.constant;

import com.example.manager.listener.DownloadListener;
import com.example.manager.task.DownloadTask;

import okhttp3.internal.concurrent.Task;

/**
 * Created by tanghao on 2021/5/27
 */
public interface Lifecycle {

    void init();

    void prepare();

    void start(DownloadTask task, DownloadListener downloadListener);

    void destroy();

}
