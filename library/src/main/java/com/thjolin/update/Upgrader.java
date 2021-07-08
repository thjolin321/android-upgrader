package com.thjolin.update;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.thjolin.compose.PatchComposeHelper;
import com.thjolin.install.GoMarketUtil;
import com.thjolin.install.InstallHelper;
import com.thjolin.update.bean.ApkUpdateBean;
import com.thjolin.update.check.CheckUpdateInterface;
import com.thjolin.update.check.ChekUpdateImpl;
import com.thjolin.update.configer.UpgraderConfiger;
import com.thjolin.update.controller.UpgraderController;
import com.thjolin.update.operate.flow.WorkFlow;
import com.thjolin.download.DownloadManager;
import com.thjolin.download.database.DownloadProvider;
import com.thjolin.download.listener.DownloadListener;
import com.thjolin.download.task.DownloadTask;
import com.thjolin.update.operate.listener.LifeCycleListener;
import com.thjolin.util.Logl;

/**
 * Created by tanghao on 2021/5/24
 */
public class Upgrader {

    private static boolean DEBUG = true;
    private UpgraderConfiger configer;
    private UpgraderController controller;

    private Upgrader() {
        controller = new UpgraderController();
    }

    @SuppressLint("StaticFieldLeak")
    static volatile Upgrader singleton;

    public static Upgrader with() {
        if (singleton == null) {
            synchronized (DownloadTask.class) {
                if (singleton == null) {
                    singleton = new Upgrader();
                }
            }
        }
        return singleton;
    }

    public void start(@NonNull ApkUpdateBean apkUpdateBean) {
        controller.start(apkUpdateBean);
    }

    public void setConfiger(UpgraderConfiger configer) {
        this.configer = configer;
        controller.setLifeCycleListener(configer.lifeCycleListener);

    }

}
