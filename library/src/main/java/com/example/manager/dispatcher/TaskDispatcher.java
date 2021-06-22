package com.example.manager.dispatcher;

import android.text.TextUtils;

import com.example.manager.constant.Lifecycle;
import com.example.manager.constant.Status;
import com.example.manager.listener.DownloadListener;
import com.example.manager.task.DownloadTask;
import com.example.manager.task.TaskCall;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tanghao on 2021/5/27
 */
public class TaskDispatcher implements Lifecycle {

    private static volatile TaskDispatcher sDownloadDispatcher;
    private List<DownloadTask> taskList;
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
        taskList = new ArrayList<>();
    }

    @Override
    public boolean prepare(DownloadTask task) {
        if (task == null) {
            return false;
        }
        if (TextUtils.isEmpty(task.getUrl())) {
            task.cancel(Status.CHECK_URL);
            return false;
        }
        if(taskExist(task.getUrl())){
            return false;
        }
        taskList.add(task);
        return true;
    }

    @Override
    public void start(DownloadTask task, DownloadListener downloadListener) {
        task.setDownloadListener(downloadListener);
        if (!prepare(task)) {
            return;
        }
        mExecutorService.submit(new TaskCall(task));
    }

    @Override
    public void destroy() {
        for (DownloadTask task : taskList) {
            task.cancel();
        }
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

    public void removeTask(String url) {
        if (taskList == null) return;
        for (int i = 0; i < taskList.size(); i++) {
            if(taskList.get(i).getUrl().equals(url)){
                taskList.remove(i);
                break;
            }
        }
    }

    public boolean taskExist(String url) {
        if (taskList == null) return false;
        for (int i = 0; i < taskList.size(); i++) {
            if(taskList.get(i).getUrl().equals(url)){
                return true;
            }
        }
        return false;
    }
}