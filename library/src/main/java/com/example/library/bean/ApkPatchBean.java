package com.example.library.bean;

import java.io.File;

/**
 * Created by tanghao on 2021/5/24
 */
public class ApkPatchBean {

    private int versionCode;
    private String patchUrl;
    private String patchMd5;

    public ApkPatchBean(int versionCode, String patchUrl) {
        this.versionCode = versionCode;
        this.patchUrl = patchUrl;
    }

    public ApkPatchBean(int versionCode, String patchUrl, String patchMd5) {
        this.versionCode = versionCode;
        this.patchUrl = patchUrl;
        this.patchMd5 = patchMd5;
    }

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

    public String getPatchMd5() {
        return patchMd5;
    }
}
