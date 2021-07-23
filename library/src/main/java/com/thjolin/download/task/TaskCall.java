package com.thjolin.download.task;

import com.thjolin.download.task.interceptor.ConnectIntercepter;
import com.thjolin.download.task.interceptor.DatabaseInterceptor;
import com.thjolin.download.task.interceptor.DownloadInterceptor;
import com.thjolin.download.task.interceptor.FileInterceptor;
import com.thjolin.download.task.interceptor.StrategyInterceptor;
import com.thjolin.download.task.interceptor.TaskInterceptor;
import com.thjolin.util.FileHelper;
import com.thjolin.util.NamedRunnable;

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
            task.dealFialedListener(task.getStatus().getMsg());
            task.cancel();
            return true;
        }
        if (task.getStatus().getCode() == 200) {
            task.dealRealSuccess();
            return true;
        }
        return false;
    }

}