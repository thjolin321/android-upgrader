package com.example.manager;


import com.example.manager.task.DownloadInfo;

import org.reactivestreams.Subscriber;

import io.reactivex.disposables.Disposable;

/**
 * Created by 陈丰尧 on 2017/2/2.
 */

public  abstract class DownLoadObserver implements Subscriber<DownloadInfo> {
    protected Disposable d;//可以用于取消注册的监听者
    protected DownloadInfo downloadInfo;

    public void onSubscribe(Disposable d) {
        this.d = d;
    }

    @Override
    public void onNext(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }
}