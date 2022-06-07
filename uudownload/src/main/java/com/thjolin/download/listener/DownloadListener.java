package com.thjolin.download.listener;

/**
 * Created by th on 2021/6/1
 */
public interface DownloadListener {

    void success(String path);

    void progress(int progress);

    void failed(String msg);

}