package com.example.manager.task.interceptor;

import com.example.manager.DownloadManager;
import com.example.manager.dispatcher.TaskDispatcher;
import com.example.manager.task.ConnectCall;
import com.example.manager.task.DownloadTask;

/**
 * Created by tanghao on 2021/5/31
 */
public class ConnectIntercepter extends AbstractIntercepter implements TaskInterceptor {

    @Override
    public DownloadTask operate(DownloadTask task) {
        TaskDispatcher.getInstance().getmExecutorService().execute(new ConnectCall(ConnectCall.class.getName(),task));
        return task;
    }
}