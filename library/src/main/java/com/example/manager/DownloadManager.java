package com.example.manager;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.compose.BsPatchUtils;
import com.example.manager.dispatcher.TaskDispatcher;
import com.example.manager.http.HttpUtil;
import com.example.manager.listener.DownloadListener;
import com.example.manager.task.DownloadTask;
import okhttp3.OkHttpClient;


public class DownloadManager {

    HttpUtil httpUtil;
    Handler handler;

    @SuppressLint("StaticFieldLeak")
    static volatile DownloadManager singleton;

    private DownloadManager() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {

            }
        };
    }

    public static DownloadManager with() {
        if (singleton == null) {
            synchronized (DownloadManager.class) {
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

    public void start(String url, DownloadListener downloadListener) {
        TaskDispatcher.getInstance().start(new DownloadTask.Builder().url(url).build(), downloadListener);
    }

    public void start(DownloadTask task, DownloadListener downloadListener) {
        TaskDispatcher.getInstance().start(task, downloadListener);
    }


}