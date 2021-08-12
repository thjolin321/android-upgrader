package com.thjolin.update.bean;

/**
 * 增量更新实体类
 * Created by tanghao on 2021/5/24
 */
public class ApkPatchBean {
    /**
     * 增量更新文件对应的版本号
     */
    private int versionCode;
    /**
     * 增量更新完整文件下载地址
     */
    private String patchUrl;
    /**
     * 增量更新文件md5值
     */
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
