package com.thjolin.download.task.interceptor;

/**
 * Created by th on 2021/6/1
 */
public abstract class AbstractInterceptor implements TaskInterceptor {

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