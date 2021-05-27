package com.example.manager.task.interceptor;

import com.example.manager.task.DownloadTask;

/**
 * Created by tanghao on 2021/5/27
 */
public class TaskMainInterceptor implements TaskInterceptor {

    private long totalSize;
    private long[] start;



    @Override
    public void init() {

    }

    @Override
    public DownloadTask operate(DownloadTask task) {

        return null;
    }

}