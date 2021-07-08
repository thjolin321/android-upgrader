package com.thjolin.update.operate.listener;

/**
 * Created by tanghao on 2021/7/8
 */
public interface UiListener {

    void show(boolean forceUpdate, boolean needDownload, boolean needCompose);

    void showNotification();

    void progress(int progress);

    void success(String msg);

    void failed(String msg);

}