package com.thjolin.update.bean;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.thjolin.download.database.DownloadProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * apk更新类
 * Created by th on 2021/5/24
 */
public class ApkUpdateBean {
    /**
     * 新版本apk完整下载地址
     */
    private String newApkUrl;
    /**
     * 新版本apk md5
     */
    private String newApkMd5;
    /**
     * 新版本apk 版本号
     */
    private int newApkVersionCode;
    /**
     * 当前版本apk 版本号，框架可自动获取
     */
    private int currentApkVersionCode;
    /**
     * 历史增量更新bean
     */
    private List<ApkPatchBean> list;

    private ApkUpdateBean(List<ApkPatchBean> list, String newApkUrl, String newApkMd5, int newApkVersionCode) {
        this.list = list;
        this.newApkUrl = newApkUrl;
        this.newApkMd5 = newApkMd5;
        this.newApkVersionCode = newApkVersionCode;
        this.currentApkVersionCode = getAppVersion();
    }

    public static class Builder {
        private List<ApkPatchBean> list;
        private String newApkUrl;
        private String newApkMd5;
        private int newApkVersionCode;

        public Builder newApkUrl(String newApkUrl) {
            this.newApkUrl = newApkUrl;
            return this;
        }

        public Builder newApkMd5(String newApkMd5) {
            this.newApkMd5 = newApkMd5;
            return this;
        }

        public Builder newApkVersionCode(int newApkVersionCode) {
            this.newApkVersionCode = newApkVersionCode;
            return this;
        }

        public Builder addApkPatchBean(ApkPatchBean bean) {
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(bean);
            return this;
        }

        public ApkUpdateBean build() {
            return new ApkUpdateBean(list, newApkUrl, newApkMd5, newApkVersionCode);
        }

    }

    /**
     * 获取当前App的版本号
     *
     * @return
     */
    public static int getAppVersion() {
        int version = 0;
        PackageManager packageManager = DownloadProvider.context.getPackageManager();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(DownloadProvider.context.getPackageName(), 0);
                version = packageInfo.versionCode;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(DownloadProvider.context.getPackageName(), 0);
                version = (int) packageInfo.getLongVersionCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return version;
    }


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

