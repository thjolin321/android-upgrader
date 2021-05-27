package com.example.manager;

import android.annotation.SuppressLint;

import com.example.manager.http.HttpUtil;
import com.example.manager.task.DownloadTask;

import okhttp3.OkHttpClient;


public class DownloadManager {

    HttpUtil httpUtil;

    @SuppressLint("StaticFieldLeak")
    static volatile DownloadManager singleton;

    private DownloadManager() {
    }

    public static DownloadManager with() {
        if (singleton == null) {
            synchronized (DownloadTask.class) {
                if (singleton == null) {
                    singleton = new DownloadManager();
                }
            }
        }
        return singleton;
    }

    public HttpUtil getHttpUtil() {
        if (httpUtil == null) {
            httpUtil = HttpUtil.with();
        }
        return httpUtil;
    }

    public void okhttp(OkHttpClient client) {
        httpUtil = HttpUtil.with(client);
    }


}