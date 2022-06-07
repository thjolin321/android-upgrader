package com.thjolin.download.dispatcher;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.thjolin.download.constant.Lifecycle;
import com.thjolin.download.constant.Status;
import com.thjolin.download.listener.DownloadListener;
import com.thjolin.download.listener.MultiDownloadListener;
import com.thjolin.download.task.DownloadTask;
import com.thjolin.download.task.TaskCall;
import com.thjolin.download.util.Logl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by th on 2021/5/27
 */
public class TaskDispatcher implements Lifecycle {

    private static final int RUNNING_SIZE = 3;
    private static volatile TaskDispatcher sDownloadDispatcher;
    private ExecutorService mExecutorService;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private List<DownloadTask> taskList;
    private LinkedList<TaskCall> runningTaskCall;
    private LinkedList<TaskCall> waitTaskCall;
    private MultiDownloadListener multiDownloadListener;

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
    public synchronized boolean prepare(DownloadTask task) {
        if (task == null) {
            return false;
        }
        if (TextUtils.isEmpty(task.getUrl())) {
            task.cancel(Status.CHECK_URL);
            return false;
        }
        if (taskExist(task.getUrl())) {
            return false;
        }
        return true;
    }

    @Override
    public synchronized void start(DownloadTask task, DownloadListener downloadListener) {
        task.setDownloadListener(downloadListener);
        if (!prepare(task)) {
            task.setStatus(Status.ERRO);
            task.getStatus().setMsg(Status.TASK_EXIST);
            task.dealFailedListener(Status.TASK_EXIST);
            return;
        }
        taskList.add(task);
        if (runningTaskCall == null) {
            runningTaskCall = new LinkedList<>();
            waitTaskCall = new LinkedList<>();
        }
        TaskCall taskCall = new TaskCall(task);
        if (runningTaskCall.size() < RUNNING_SIZE) {
            Logl.e("直接执行");
            runningTaskCall.add(taskCall);
            mExecutorService.submit(taskCall);
        } else {
            Logl.e("等待");
            waitTaskCall.add(taskCall);
        }
    }

    @Override
    public void destroy() {
        for (DownloadTask task : taskList) {
            task.cancel();
        }
    }

    public void setMultiDownloadListener(MultiDownloadListener multiDownloadListener) {
        this.multiDownloadListener = multiDownloadListener;
    }

    public synchronized void executeNextTaskCall() {
        Logl.e("executeNextTaskCall: " + waitTaskCall.size());
        if (waitTaskCall == null) return;
        TaskCall taskCall = waitTaskCall.poll();
        if (taskCall == null) {
            return;
        }
        runningTaskCall.add(taskCall);
        mExecutorService.submit(taskCall);
        Logl.e("executeNextTaskCall执行：" + taskCall.getTask().getUrl());
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

    public synchronized void removeTask(String url) {
        removeTask(url, false);
    }

    public synchronized void removeTask(String url, boolean success) {
        Logl.e("removeTask  url " + url);
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getUrl().equals(url)) {
                if (multiDownloadListener != null) {
                    if (success) {
                        multiDownloadListener.onSuccess(url, taskList.get(i).getFinalFilePath());
                    } else {
                        multiDownloadListener.onFailed(url);
                    }
                    if (taskList.size() == 1) {
                        multiDownloadListener.onFinish();
                    }
                }
                taskList.remove(i);
                Logl.e("taskList return " + taskList.toString());
                break;
            }
        }
        for (int i = 0; i < runningTaskCall.size(); i++) {
            if (runningTaskCall.get(i).getTask().getUrl().equals(url)) {
                runningTaskCall.remove(i);
                executeNextTaskCall();
                return;
            }
        }
    }

    public boolean taskExist(String url) {
        Logl.e("taskExist  url " + url);
        Logl.e("taskList  " + taskList.toString());
        if (taskList == null) return false;
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getUrl().equals(url)) {
                return true;
            }
        }
        return false;
    }

    public void moveToMainThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public void inspectRunningAndWait() {
        Logl.e("running: " + runningTaskCall.size());
        Logl.e("wait: " + waitTaskCall.size());
        for (TaskCall call : runningTaskCall) {
            Logl.e("Running: " + call.getTask().getUrl());
        }
        for (TaskCall call : waitTaskCall) {
            Logl.e("waitTaskCall: " + call.getTask().getUrl());
        }
    }

}