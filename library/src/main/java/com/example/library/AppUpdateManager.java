package com.example.library;

import android.app.Dialog;

import com.example.library.bean.ApkUpdateBean;
import com.example.library.check.CheckUpdateInterface;
import com.example.library.download.DownLoadInterface;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tanghao on 2021/5/24
 */
public class AppUpdateManager {

    private boolean showDownladProgress;
    private boolean silent;
    private boolean forceUpdate;
    private Dialog updateDialog;
    private int updateMethod; // 0 for no value,-1 for no need update, 1、for patch,
    // 2 for completeApk, 3 for marcket, 4 for 插件化更新, -2 for wrong type

    private ApkUpdateBean apkUpdateBean;



    private static boolean DEBUG = true;
    private static AppUpdateManager INSTANCE = new AppUpdateManager();
    private CheckUpdateInterface checkUpdateInterface;
    private DownLoadInterface downLoadInterface;

    public static AppUpdateManager getInstance() {
        return INSTANCE;
    }

    public void start() {
        if (checkUpdateInterface.checkUpdateByHistoryVersions(apkUpdateBean) < 1) {
            return;
        }


    }


    public static class Builder {

        private boolean showDownladProgress;
        private boolean silent;
        private boolean forceUpdate;
        private Dialog updateDialog;
        private int updateMethod; // 0 for no need update, 1、for patch, 2 for completeApk, 3 for marcket, 4 for 插件化更新, -1 for wrong type

    }

}
