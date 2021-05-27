package com.example.library.bean;

import java.io.File;

/**
 * Created by tanghao on 2021/5/24
 */
public class ApkPatchBean {

    private int versionCode;
    private String patchUrl;
    private File patchPath;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getPatchUrl() {
        return patchUrl;
    }

    public void setPatchUrl(String patchUrl) {
        this.patchUrl = patchUrl;
    }

    public File getPatchPath() {
        return patchPath;
    }

    public void setPatchPath(File patchPath) {
        this.patchPath = patchPath;
    }
}
