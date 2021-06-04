package com.example.manager.task.interceptor;

import com.example.manager.task.DownloadTask;
import com.example.manager.util.FileUtils;

/**
 * Created by tanghao on 2021/6/1
 */
public class RetryInterceptor extends AbstractIntercepter implements TaskInterceptor {
    @Override
    public DownloadTask operate(DownloadTask task) {

        return task;
    }
}