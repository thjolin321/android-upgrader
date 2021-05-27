package com.example.library.bean;

import java.util.List;

/**
 * Created by tanghao on 2021/5/24
 */
public class ApkUpdateBean {

    private List<ApkPatchBean> list;
    private String newApkMd5;
    private String newApkUrl;
    private int newApkVersionCode;

    private int currentApkVersionCode;

    public List<ApkPatchBean> getList() {
        return list;
    }

    public void setList(List<ApkPatchBean> list) {
        this.list = list;
    }

    public String getNewApkMd5() {
        return newApkMd5;
    }

    public void setNewApkMd5(String newApkMd5) {
        this.newApkMd5 = newApkMd5;
    }

    public String getNewApkUrl() {
        return newApkUrl;
    }

    public void setNewApkUrl(String newApkUrl) {
        this.newApkUrl = newApkUrl;
    }

    public int getNewApkVersionCode() {
        return newApkVersionCode;
    }

    public void setNewApkVersionCode(int newApkVersionCode) {
        this.newApkVersionCode = newApkVersionCode;
    }

    public int getCurrentApkVersionCode() {
        return currentApkVersionCode;
    }

    public void setCurrentApkVersionCode(int currentApkVersionCode) {
        this.currentApkVersionCode = currentApkVersionCode;
    }
}

