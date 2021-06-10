package com.example.manager.task.interceptor;

import com.example.manager.dispatcher.TaskDispatcher;
import com.example.manager.task.DownloadTask;

import java.util.LinkedList;

/**
 * Created by tanghao on 2021/6/1
 */
public abstract class AbstractIntercepter implements TaskInterceptor {

    public TaskInterceptor taskInterceptor;
    public TaskInterceptor last;


    @Override
    public TaskInterceptor next() {
        return taskInterceptor;
    }

    @Override
    public void setNext(TaskInterceptor taskInterceptor) {
        this.taskInterceptor = taskInterceptor;
    }

    @Override
    public void add(TaskInterceptor task) {
        if (taskInterceptor == null) {
            this.taskInterceptor = task;
            return;
        }
        if (last == null) {
            last = taskInterceptor;
            taskInterceptor.setNext(task);
        }
        last.setNext(task);
        last = task;
    }
}