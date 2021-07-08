package com.thjolin.update.operate.listener;

/**
 * Created by tanghao (); on 2021/7/7
 */
public interface LifeCycleListener {

    int NO_NEED_UPGRADE = -1;
    int CHECK = 0;
    int START = 1;
    int DOWNLOAD = 2;
    int COMPOSE = 3;
    int INSTALL = 4;
    int MARKET = 99;
    int FINISH = 100;
    int UPGRADE_ERROR = -100;

    boolean onCheck();

    void onStart();

    void onDownload();

    void onDownloadProgress();

    void onCompose();

    void onInstall();

    void onError();

    void onFinish();
}
