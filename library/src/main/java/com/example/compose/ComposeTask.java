package com.example.compose;

/**
 * Created by tanghao on 2021/6/15
 */
public class ComposeTask {

    private String newFilePath;
    private String patchPath;
    private String completeApkMd5;

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