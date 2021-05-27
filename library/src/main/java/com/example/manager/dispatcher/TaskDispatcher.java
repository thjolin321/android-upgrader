package com.example.manager.dispatcher;

import com.example.manager.constant.Lifecycle;
import com.example.manager.http.HttpUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tanghao on 2021/5/27
 */
public class TaskDispatcher implements Lifecycle {

    private static volatile TaskDispatcher sDownloadDispatcher;

    //线程池
    private ExecutorService mExecutorService;


    @Override
    public void init() {
        mExecutorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(false);
            return thread;
        });
    }

    @Override
    public void prepare(){
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {

    }

    public static TaskDispatcher getInstance() {
        if (sDownloadDispatcher == null) {
            synchronized (TaskDispatcher.class) {
                if (sDownloadDispatcher == null) {
                    sDownloadDispatcher = new TaskDispatcher();
                }
            }
        }
        return sDownloadDispatcher;
    }

    private synchronized ExecutorService executorService() {
        return mExecutorService;
    }

}