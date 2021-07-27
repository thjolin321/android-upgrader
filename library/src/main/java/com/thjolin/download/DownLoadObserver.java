package com.thjolin.download;


import com.thjolin.download.database.DownloadEntity;

import org.reactivestreams.Subscriber;

import io.reactivex.disposables.Disposable;

/**
 * Created by 陈丰尧 on 2017/2/2.
 */

public  abstract class DownLoadObserver implements Subscriber<DownloadEntity> {
    protected Disposable d;//可以用于取消注册的监听者
    protected DownloadEntity downloadEntity;

    public void onSubscribe(Disposable d) {
        this.d = d;
    }

    @Override
    public void onNext(DownloadEntity downloadEntity) {
        this.downloadEntity = downloadEntity;
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }
}