package com.thjolin.update.configer;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.thjolin.ui.DefaultActivityController;
import com.thjolin.update.operate.listener.ForceExitListener;
import com.thjolin.update.operate.listener.LifeCycleListener;
import com.thjolin.update.operate.listener.UiListener;
import com.thjolin.download.util.Logl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import okhttp3.OkHttpClient;

/**
 * Created by tanghao on 2021/6/11
 */
public class UpgraderConfiger {

    public final static int PATCH_UPDATE = 1; // 增量更新
    public final static int COMPLETE_UPDATE = 2;  // 完整apk更新
    public final static int MARKET_UPDATE = 3;  // 跳转应用市场

    /**
     * 是否静默下载
     */
    public boolean silent;
    /**
     * 是否显示下载进度
     */
    public boolean showDownladProgress;
    /**
     * 是否强制更新，可配合 ForceExitListener 使用
     */
    public boolean forceUpdate;
    /**
     * 是否显示通知栏
     */
    public boolean needNotification;
    /**
     * 更新生命周期回调
     */
    public LifeCycleListener lifeCycleListener;
    /**
     * 下载过程UI回调，可自定义UI。本框架也会默认UI，可作参考
     */
    public UiListener uiListener;
    /**
     * 强制更新，点击关闭时的回调
     */
    public ForceExitListener forceExitListener;
    /**
     * 透传到下载框架Uudownloader的okHttpClient
     */
    public OkHttpClient downloadOkHttpClient;
    /**
     * 更新方式
     */
    public int updateMethod; // 0 for no value,-1 for no need update, 1、for patch,
    // 2 for completeApk, 3 for market,  -2 for wrong type

    @IntDef({PATCH_UPDATE, COMPLETE_UPDATE, MARKET_UPDATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UpdateMethod {
    }


    public static UpgraderConfiger createDefaultConfiger() {
        return new UpgraderConfiger(true, false, true, false,
                DefaultActivityController.getInstance(), 0, null, new LifeCycleListener() {
            @Override
            public boolean onCheck() {
                Logl.e("onCheck");
                return false;
            }

            @Override
            public void onStart() {
                Logl.e("onStart");
            }

            @Override
            public void onDownload() {
                Logl.e("onDownload");
            }

            @Override
            public void onDownloadProgress() {
                Logl.e("onDownloadProgress");
            }

            @Override
            public void onCompose() {
                Logl.e("onCompose");
            }

            @Override
            public void onInstall() {
                Logl.e("onInstall");
            }

            @Override
            public void onError() {
                Logl.e("onError");
            }

            @Override
            public void onFinish() {
                Logl.e("onFinish");
            }
        });
    }

    public UpgraderConfiger(boolean showDownladProgress, boolean silent, boolean forceUpdate, boolean needNotification,
                            UiListener uiListener, int updateMethod, ForceExitListener forceExitListener, LifeCycleListener lifeCycleListener
    ) {
        this.showDownladProgress = showDownladProgress;
        this.silent = silent;
        this.forceUpdate = forceUpdate;
        this.uiListener = uiListener;
        this.updateMethod = updateMethod;
        this.lifeCycleListener = lifeCycleListener;
        this.needNotification = needNotification;
    }

    public static class Builder {
        private boolean silent;
        private boolean showDownladProgress;
        private boolean forceUpdate;
        private boolean needNotifycation;
        private UiListener uiListener;
        private ForceExitListener forceExitListener;
        private OkHttpClient downloadOkHttpClient;
        private LifeCycleListener lifeCycleListener;
        private int updateMethod; // 0 for no need update, 1、for patch, 2 for completeApk,
        // 3 for marcket, 4 for 插件化更新, -1 for wrong type

        public Builder silent(boolean silent) {
            this.silent = silent;
            return this;
        }

        public Builder showDownladProgress(boolean showDownladProgress) {
            this.showDownladProgress = showDownladProgress;
            return this;
        }

        public Builder forceUpdate(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
            return this;
        }

        public Builder uiListener(@NonNull UiListener uiListener) {
            this.uiListener = uiListener;
            return this;
        }

        public Builder updateMethod(@UpdateMethod int updateMethod) {
            this.updateMethod = updateMethod;
            return this;
        }

        public Builder downloadOkHttpClient(OkHttpClient downloadOkHttpClient) {
            this.downloadOkHttpClient = downloadOkHttpClient;
            return this;
        }

        public Builder needNotifycation(boolean needNotifycation) {
            this.needNotifycation = needNotifycation;
            return this;
        }

        public Builder forceExitListener(ForceExitListener forceExitListener) {
            this.forceExitListener = forceExitListener;
            return this;
        }

        public Builder lifeCycleListener(LifeCycleListener lifeCycleListener) {
            this.lifeCycleListener = lifeCycleListener;
            return this;
        }

        public UpgraderConfiger build() {
            return new UpgraderConfiger(showDownladProgress,
                    silent, forceUpdate, needNotifycation, uiListener,
                    updateMethod, forceExitListener, lifeCycleListener);
        }
    }
}