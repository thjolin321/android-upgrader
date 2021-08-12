package com.thjolin.download.task.interceptor;

import com.thjolin.download.task.DownloadTask;

/**
 * Created by tanghao on 2021/6/1
 */
public class RetryInterceptor extends AbstractIntercepter implements TaskInterceptor {
    @Override
    public DownloadTask operate(DownloadTask task) {

        return task;
    }
}