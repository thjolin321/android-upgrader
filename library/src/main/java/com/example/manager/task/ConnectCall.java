package com.example.manager.task;

import com.example.manager.DownloadManager;
import com.example.manager.constant.Status;
import com.example.manager.util.NamedRunnable;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by tanghao on 2021/5/31
 */
public class ConnectCall extends NamedRunnable {

    private DownloadTask downloadTask;

    public ConnectCall(String name, DownloadTask downloadTask) {
        super(name);
        this.downloadTask = downloadTask;
    }

    @Override
    protected void execute() throws InterruptedException {
        Response response = null;
        try {
            response = DownloadManager.with().getHttpUtil().asyncCall(downloadTask.getUrl());
        } catch (IOException e) {
            downloadTask.status.setStatus(Status.ERRO);
            e.printStackTrace();
        }
        long totalSize = response.body().contentLength();
        if (totalSize <= 0) {
            downloadTask.setStatus(Status.ERRO);
            downloadTask.getStatus().setMsg(Status.CHECK_URL);
        }
        downloadTask.setTotalSize(totalSize);
    }

    @Override
    protected void interrupted(InterruptedException e) {

    }

    @Override
    protected void finished() {

    }
}