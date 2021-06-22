package com.example.manager.task;

import com.example.manager.DownloadManager;
import com.example.manager.constant.Status;
import com.example.manager.task.interceptor.ConnectIntercepter;
import com.example.manager.task.interceptor.DatabaseInterceptor;
import com.example.manager.task.interceptor.DownloadInterceptor;
import com.example.manager.task.interceptor.FileInterceptor;
import com.example.manager.task.interceptor.StrategyInterceptor;
import com.example.manager.task.interceptor.TaskInterceptor;
import com.example.manager.util.FileUtils;
import com.example.manager.util.Logl;
import com.example.manager.util.NamedRunnable;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by tanghao on 2021/5/31
 */
public class TaskCall extends NamedRunnable {

    private DownloadTask task;
    TaskInterceptor taskInterceptor;

    public TaskCall(DownloadTask downloadTask) {
        super(downloadTask.url);
        this.task = downloadTask;
        taskInterceptor = new FileInterceptor();
        taskInterceptor.add(new ConnectIntercepter());
        taskInterceptor.add(new DatabaseInterceptor());
        taskInterceptor.add(new StrategyInterceptor());
        taskInterceptor.add(new DownloadInterceptor());
        downloadTask.setTaskCall(this);
    }

    @Override
    protected void execute() {
        start();
    }

    @Override
    protected void interrupted(InterruptedException e) {
        if (task.getCallList() != null) {
            for (DownloadCall downloadCall : task.getCallList()) {
                downloadCall.cancel();
            }
        }
    }

    private void start() {
        // 添加守护程序
        while (taskInterceptor != null) {
            taskInterceptor.operate(task);
            taskInterceptor = taskInterceptor.next();
            if (checkStatus()) {
                return;
            }
        }
    }


    private boolean checkStatus() {
        if (task.getStatus().getCode() == -1) {
            if (task.downloadListener != null) {
                task.downloadListener.failed(task.getStatus().getMsg());
            }
            return true;
        }
        if (task.getStatus().getCode() == 200) {
            if (task.downloadListener != null) {
                task.downloadListener.success(FileUtils.getTargetFilePath(task.getFileParent(), task.getFileName()));
            }
            return true;
        }
        return false;
    }

}