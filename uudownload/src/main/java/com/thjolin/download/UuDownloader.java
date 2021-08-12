package com.thjolin.download;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.thjolin.download.database.base.DaoFactory;
import com.thjolin.download.dispatcher.TaskDispatcher;
import com.thjolin.download.http.HttpUtil;
import com.thjolin.download.listener.DownloadListener;
import com.thjolin.download.listener.MultiDownloadListener;
import com.thjolin.download.task.DownloadTask;

import java.util.List;

import okhttp3.OkHttpClient;


public class UuDownloader {

    HttpUtil httpUtil;
    Handler handler;

    @SuppressLint("StaticFieldLeak")
    static volatile UuDownloader singleton;

    private UuDownloader() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {

            }
        };
    }

    public static UuDownloader with() {
        if (singleton == null) {
            synchronized (UuDownloader.class) {
                if (singleton == null) {
                    singleton = new UuDownloader();
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

    /**
     * 使用已有的OkHttpClient下载
     * @param client
     */
    public void okhttp(OkHttpClient client) {
        httpUtil = HttpUtil.with(client);
    }

    public void start(String url, DownloadListener downloadListener) {
        TaskDispatcher.getInstance().start(new DownloadTask.Builder().url(url).build(), downloadListener);
    }

    public void start(DownloadTask task, DownloadListener downloadListener) {
        TaskDispatcher.getInstance().start(task, downloadListener);
    }

    public void start(List<String> urls, MultiDownloadListener multiDownloadListener) {
        if (urls == null || urls.size() == 0) return;
        DownloadTask.Builder builder = new DownloadTask.Builder();
        TaskDispatcher.getInstance().setMultiDownloadListener(multiDownloadListener);
        for (String url : urls) {
            TaskDispatcher.getInstance().start(builder.url(url).build(), null);
        }
    }

    public void destroy() {
        TaskDispatcher.getInstance().destroy();
        DaoFactory.getFactory().closeDatabase();
    }

}