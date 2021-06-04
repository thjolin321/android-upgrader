package com.example.manager.dispatcher;

import android.text.TextUtils;

import com.example.manager.constant.Lifecycle;
import com.example.manager.constant.Status;
import com.example.manager.listener.DownloadListener;
import com.example.manager.task.DownloadTask;
import com.example.manager.task.interceptor.ConnectIntercepter;
import com.example.manager.task.interceptor.DatabaseInterceptor;
import com.example.manager.task.interceptor.DownloadInterceptor;
import com.example.manager.task.interceptor.FileInterceptor;
import com.example.manager.task.interceptor.StrategyInterceptor;
import com.example.manager.task.interceptor.TaskInterceptor;
import com.example.manager.util.FileUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.internal.concurrent.Task;

/**
 * Created by tanghao on 2021/5/27
 */
public class TaskDispatcher implements Lifecycle {

    private static volatile TaskDispatcher sDownloadDispatcher;
    private TaskInterceptor taskInterceptor;
    private DownloadTask task;
    private DownloadListener downloadListener;

    //线程池
    private ExecutorService mExecutorService;

    private TaskDispatcher() {
        init();
    }


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
    public void prepare() {
        if (task == null) {
            return;
        }
        if (TextUtils.isEmpty(task.getUrl())) {
            if (downloadListener != null) {
                downloadListener.failed(Status.CHECK_URL);
            }
            return;
        }
        taskInterceptor = new FileInterceptor();
        taskInterceptor.add(new ConnectIntercepter());
        taskInterceptor.next().add(new StrategyInterceptor());
        taskInterceptor.next().next().add(new DatabaseInterceptor());
        taskInterceptor.next().next().next().add(new DownloadInterceptor());

    }

    @Override
    public void start(DownloadTask task, DownloadListener downloadListener) {
        this.task = task;
        this.downloadListener = downloadListener;
        task.setDownloadListener(downloadListener);
        prepare();
        while (taskInterceptor != null) {
            taskInterceptor.operate(task);
            taskInterceptor = taskInterceptor.next();
            if (checkStatus()) {
                return;
            }
        }
    }

    @Override
    public void destroy() {

    }

    private boolean checkStatus() {
        if (task.getStatus().getCode() == -1) {
            if (downloadListener != null) {
                downloadListener.failed(task.getStatus().getMsg());
            }
            return true;
        }
        if (task.getStatus().getCode() == 200) {
            if (downloadListener != null) {
                downloadListener.success(FileUtils.getTargetFilePath(task.getFileParent(), task.getFileName()));
            }
            return true;
        }
        return false;
    }


    public ExecutorService getmExecutorService() {
        return mExecutorService;
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