package com.thjolin.update;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.thjolin.update.bean.ApkUpdateBean;
import com.thjolin.update.configer.UpgraderConfiger;
import com.thjolin.update.controller.UpgraderController;
import com.thjolin.download.task.DownloadTask;
import com.thjolin.download.util.Logl;

/**
 * Created by th on 2021/5/24
 */
public class Upgrader {

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

    public Upgrader setConfiger(UpgraderConfiger configer) {
        controller.setConfiger(configer);
        controller.setLifeCycleListener(configer.lifeCycleListener);
        return this;
    }

    public Upgrader setDebug(boolean debug) {
        Logl.setDEBUG(debug);
        return this;
    }

    public void destroy() {
        controller.destroy();
    }

}
