package com.thjolin.update.controller;

import androidx.annotation.NonNull;

import com.thjolin.compose.PatchComposeHelper;
import com.thjolin.download.DownloadManager;
import com.thjolin.download.database.DownloadProvider;
import com.thjolin.download.listener.DownloadListener;
import com.thjolin.download.task.DownloadTask;
import com.thjolin.install.GoMarketUtil;
import com.thjolin.install.InstallApkActivity;
import com.thjolin.install.InstallHelper;
import com.thjolin.update.bean.ApkUpdateBean;
import com.thjolin.update.check.CheckUpdateInterface;
import com.thjolin.update.check.ChekUpdateImpl;
import com.thjolin.update.configer.UpgraderConfiger;
import com.thjolin.update.operate.flow.WorkFlow;
import com.thjolin.update.operate.listener.LifeCycleListener;
import com.thjolin.update.operate.listener.UiListener;
import com.thjolin.util.Logl;

import static com.thjolin.update.operate.listener.LifeCycleListener.CHECK;
import static com.thjolin.update.operate.listener.LifeCycleListener.COMPOSE;
import static com.thjolin.update.operate.listener.LifeCycleListener.DOWNLOAD;
import static com.thjolin.update.operate.listener.LifeCycleListener.FINISH;
import static com.thjolin.update.operate.listener.LifeCycleListener.INSTALL;
import static com.thjolin.update.operate.listener.LifeCycleListener.NO_NEED_UPGRADE;
import static com.thjolin.update.operate.listener.LifeCycleListener.START;
import static com.thjolin.update.operate.listener.LifeCycleListener.UPGRADE_ERROR;

/**
 * Created by tanghao on 2021/7/8
 */
public class UpgraderController {

    LifeCycleListener lifeCycleListener;
    UiListener uiListener;
    private CheckUpdateInterface checkUpdateInterface;
    private UpgraderConfiger configer;
    private volatile boolean isDestroyed;


    public UpgraderController() {
        checkUpdateInterface = new ChekUpdateImpl();
    }

    public void moveToState(int newState) {
        if (lifeCycleListener == null) return;
        switch (newState) {
            case CHECK:
                lifeCycleListener.onCheck();
                break;
            case DOWNLOAD:
                lifeCycleListener.onDownload();
                break;
            case COMPOSE:
                lifeCycleListener.onCompose();
                break;
            case INSTALL:
                lifeCycleListener.onInstall();
                break;
            case FINISH:
                lifeCycleListener.onFinish();
                break;
            case UPGRADE_ERROR:
                lifeCycleListener.onError();
                break;
        }
    }

    public void dealLifeCycle(int newState) {
        moveToState(newState);
    }

    public void setLifeCycleListener(LifeCycleListener lifeCycleListener) {
        this.lifeCycleListener = lifeCycleListener;
    }

    public void setConfiger(UpgraderConfiger configer) {
        this.configer = configer;
    }


    public void start(@NonNull ApkUpdateBean apkUpdateBean) {
        if (configer == null) {
            configer = UpgraderConfiger.createDefaultConfiger();
            setLifeCycleListener(configer.lifeCycleListener);
            uiListener = configer.uiListener;
        }
        dealLifeCycle(CHECK);
        int checkResult = checkUpdateInterface.checkUpdateByHistoryVersions(apkUpdateBean);
        if (checkResult < 1) {
            dealLifeCycle(NO_NEED_UPGRADE);
            return;
        }
        if (checkResult == 3) {
            dealLifeCycle(LifeCycleListener.MARKET);
            InstallHelper.gotoMarket();
            return;
        }
        dealLifeCycle(START);
        WorkFlow flow = checkUpdateInterface.getFlow();
        if (!configer.silent && checkUiListener()) {
            uiListener.setOnRightClick(new InstallApkActivity.OnDialogClick() {
                @Override
                public void onCancel() {
                    lifeCycleListener.onFinish();
                    if (configer.forceUpdate && configer.forceExitListener != null) {
                        configer.forceExitListener.exit();
                    }
                }

                @Override
                public void onSure() {
                    if (flow.getDownloadTask() != null) {
                        handleDownload(flow, apkUpdateBean);
                    }
                }
            });
            uiListener.show(configer.needNotification, configer.forceUpdate,
                    flow.getDownloadTask() != null,
                    flow.getComposeTask() != null, null, flow.getDownloadTask().build().getFileNameForce());
            return;
        }
        if (flow.getDownloadTask() != null) {
            handleDownload(flow, apkUpdateBean);
        }
    }

    private void handleDownload(WorkFlow flow, ApkUpdateBean apkUpdateBean) {
        dealLifeCycle(DOWNLOAD);
        DownloadTask.Builder taskBuilder = flow.getDownloadTask();
        taskBuilder.needProgress(configer.showDownladProgress);
        DownloadManager.with().start(taskBuilder.build(), new DownloadListener() {
            @Override
            public void success(String path) {
                if (flow.getComposeTask() != null) {
                    dealLifeCycle(COMPOSE);
                    flow.getComposeTask().setPatchPath(path);
                    if (PatchComposeHelper.patch(flow.getComposeTask()) == 0) {
                        path = flow.getComposeTask().getNewFilePath();
                        if (uiListener != null) {
                            uiListener.progress(110);
                        }
                    } else {
                        apkUpdateBean.setList(null);
                        start(apkUpdateBean);
                        return;
                    }
                }
                if (uiListener == null) {
                    return;
                }
                if (configer.silent) {
                    uiListener.show(false, configer.forceUpdate, configer.forceUpdate, false, path, null);
                } else {
                    uiListener.downloadSuccess(path);
                }
            }

            @Override
            public void progress(int progress) {
                Logl.e("progress");
                if (checkUiListener()) {
                    uiListener.progress(progress);
                }
            }

            @Override
            public void failed(String msg) {
                Logl.e("handleDownload msg: " + msg);
                // 判断具体失败原因，做相应的处理
                if (checkUiListener()) {
                    uiListener.failed(msg);
                }
                if (flow.getComposeTask() != null) {
                    apkUpdateBean.setList(null);
                    start(apkUpdateBean);
                } else {
                    dealLifeCycle(CHECK);
                    GoMarketUtil.start(DownloadProvider.context, "com.tencent.weixin");
                }
            }
        });
    }

    private boolean checkUiListener() {
        return uiListener != null && !isDestroyed;
    }

    public void destroy() {
        isDestroyed = true;
        DownloadManager.with().destroy();
    }

}