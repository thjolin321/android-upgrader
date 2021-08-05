package com.thjolin.compose;

import android.text.TextUtils;

import com.thjolin.download.database.DownloadProvider;

import java.io.File;

/**
 * Created by tanghao on 2021/6/15
 */
public class ComposeTask {

    private String newFilePath;
    private String patchPath;
    private String completeApkMd5;


    public ComposeTask(String patchPath, String completeApkMd5) {
        File newFile = new File(DownloadProvider.context.getExternalFilesDir("apk"), "app.apk");
        this.newFilePath = newFile.getAbsolutePath();
        this.patchPath = patchPath;
        this.completeApkMd5 = completeApkMd5;
    }

    public ComposeTask(String newFilePath, String patchPath, String completeApkMd5) {
        File newFile = new File(DownloadProvider.context.getExternalFilesDir("apk"), "app.apk");
        if (TextUtils.isEmpty(newFilePath)) {
            this.newFilePath = newFile.getAbsolutePath();
        } else {
            this.newFilePath = newFilePath;
        }
        this.patchPath = patchPath;
        this.completeApkMd5 = completeApkMd5;
    }

    public String getNewFilePath() {
        return newFilePath;
    }

    public void setNewFilePath(String newFilePath) {
        this.newFilePath = newFilePath;
    }

    public String getPatchPath() {
        return patchPath;
    }

    public void setPatchPath(String patchPath) {
        this.patchPath = patchPath;
    }

    public String getCompleteApkMd5() {
        return completeApkMd5;
    }

    public void setCompleteApkMd5(String completeApkMd5) {
        this.completeApkMd5 = completeApkMd5;
    }
}