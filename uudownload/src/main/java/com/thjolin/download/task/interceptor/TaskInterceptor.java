package com.thjolin.download.task.interceptor;

import com.thjolin.download.task.DownloadTask;

/**
 * Created by tanghao on 2021/5/27
 */
public interface TaskInterceptor {

    DownloadTask operate(DownloadTask task);

    TaskInterceptor next();

    void setNext(TaskInterceptor taskInterceptor);

    void add(TaskInterceptor taskInterceptor);

}
