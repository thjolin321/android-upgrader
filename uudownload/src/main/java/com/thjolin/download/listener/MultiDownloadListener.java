package com.thjolin.download.listener;

/**
 * Created by tanghao on 2021/6/1
 */
public interface MultiDownloadListener {

    // all task finish
    void onFinish();

    // one task success
    void onSuccess(String url,String path);

    // task failed
    void onFailed(String url);

}