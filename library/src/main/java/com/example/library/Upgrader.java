package com.example.library;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.compose.PatchComposeHelper;
import com.example.install.GoMarketUtil;
import com.example.install.InstallHelper;
import com.example.library.bean.ApkUpdateBean;
import com.example.library.check.CheckUpdateInterface;
import com.example.library.check.ChekUpdateImpl;
import com.example.library.configer.UpgraderConfiger;
import com.example.library.download.DownLoadInterface;
import com.example.library.operate.flow.Flow;
import com.example.library.operate.flow.WorkFlow;
import com.example.manager.DownloadManager;
import com.example.manager.database.DownloadProvider;
import com.example.manager.listener.DownloadListener;
import com.example.manager.task.DownloadTask;
import com.example.manager.util.Logl;

/**
 * Created by tanghao on 2021/5/24
 */
public class Upgrader {

    static{
//    System.loadLibrary("ApkPatch");
    }
    private UpgraderConfiger configer;

    private static boolean DEBUG = true;
    private CheckUpdateInterface checkUpdateInterface;
    private DownLoadInterface downLoadInterface;

    private Upgrader() {
        checkUpdateInterface = new ChekUpdateImpl();
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
        int checkResult = checkUpdateInterface.checkUpdateByHistoryVersions(apkUpdateBean);
        if (checkResult < 1) {
            return;
        }
        WorkFlow flow = checkUpdateInterface.getFlow();
        if (flow.getDownloadTask() != null) {
            handleDownload(flow, apkUpdateBean);
            return;
        }
        if (checkResult == 3) {
            InstallHelper.gotoMarket();
        }
    }

    private void handleDownload(WorkFlow flow, ApkUpdateBean apkUpdateBean) {
        DownloadManager.with().start(flow.getDownloadTask(), new DownloadListener() {
            @Override
            public void success(String path) {
                Logl.e("handleDownload path: " + path);
                if (flow.getComposeTask() != null) {
                    flow.getComposeTask().setPatchPath(path);
                    if (PatchComposeHelper.patch(flow.getComposeTask()) == 0) {
                        InstallHelper.installApk(flow.getComposeTask().getNewFilePath());
                    } else {
                        // 开始尝试全量更新
                        apkUpdateBean.setList(null);
                        start(apkUpdateBean);
                    }
                    return;
                }
                // 完整apk更新
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InstallHelper.install(path);
                    }
                },5000);
            }

            @Override
            public void progress(int progress) {
                Logl.e("handleDownload progress: " + progress);
            }

            @Override
            public void failed(String msg) {
                Logl.e("handleDownload msg: " + msg);
                // 判断具体失败原因，做相应的处理
                if (flow.getComposeTask() != null) {
                    apkUpdateBean.setList(null);
                    start(apkUpdateBean);
                } else {
                    // 尝试应用市场更新 TODO
                    GoMarketUtil.start(DownloadProvider.context,"com.tencent.weixin");
                }
            }
        });
    }

    public void setConfiger(UpgraderConfiger configer) {
        this.configer = configer;
    }

}
