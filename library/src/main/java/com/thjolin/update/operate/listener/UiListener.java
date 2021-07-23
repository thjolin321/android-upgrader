package com.thjolin.update.operate.listener;

import com.thjolin.install.InstallApkActivity;
import com.thjolin.ui.PDialog;

/**
 * Created by tanghao on 2021/7/8
 */
public interface UiListener {

    void show(boolean showNotification, boolean forceUpdate, boolean needDownload, boolean needCompose, String apkPath, String fileName);

    void showNotification();

    void progress(int progress);

    void downloadSuccess(String path);

    void failed(String msg);

    void setOnRightClick(InstallApkActivity.OnDialogClick onRightClick);

}