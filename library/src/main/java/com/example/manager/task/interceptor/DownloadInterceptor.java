package com.example.manager.task.interceptor;

import android.util.Log;

import com.example.manager.dispatcher.TaskDispatcher;
import com.example.manager.task.DownloadCall;
import com.example.manager.task.DownloadTask;
import com.example.manager.util.Logl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghao on 2021/6/1
 */
public class DownloadInterceptor extends AbstractIntercepter implements TaskInterceptor {
    @Override
    public DownloadTask operate(DownloadTask task) {
        Logl.e("开始执行DownloadInterceptor");
        createDownloadCall(task);
        return task;
    }


    private void createDownloadCall(DownloadTask task) {

        List<DownloadCall> list = new ArrayList<>(task.getBlockSize());
        for (int i = 0; i < task.getBlockSize(); i++) {
            TaskDispatcher.getInstance().getmExecutorService().submit(new DownloadCall(DownloadCall.class.getName() + "i", task, i));
//            list.add(new DownloadCall(DownloadCall.class.getName() + "i", task, i));
        }

    }

}