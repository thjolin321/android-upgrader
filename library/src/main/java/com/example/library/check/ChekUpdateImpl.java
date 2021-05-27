package com.example.library.check;

import android.text.TextUtils;

import com.example.library.bean.ApkPatchBean;
import com.example.library.bean.ApkUpdateBean;

import java.util.Collections;

/**
 * Created by tanghao on 2021/5/25
 */
public class ChekUpdateImpl implements CheckUpdateInterface {

    String downloadUrl;

    //-1 for no need update, 1、for patch, 2 for completeApk, 3 for marcket, 4 for 插件化更新, -2 for wrong type
    @Override
    public int checkUpdateByHistoryVersions(ApkUpdateBean apkUpdateBean) {
        if (apkUpdateBean.getCurrentApkVersionCode() >= apkUpdateBean.getNewApkVersionCode()) {
            downloadUrl = null;
            return -1;
        }
        if (apkUpdateBean.getList() != null) {
            for (ApkPatchBean apkPatchBean : apkUpdateBean.getList()) {
                if (apkPatchBean.getVersionCode() == apkUpdateBean.getCurrentApkVersionCode()) {
                    downloadUrl = apkPatchBean.getPatchUrl();
                    return 1;
                }
            }
        }
        if (TextUtils.isEmpty(apkUpdateBean.getNewApkUrl())) {
            downloadUrl = null;
            return -2;
        }
        downloadUrl = apkUpdateBean.getNewApkUrl();
        return 2;
    }

    @Override
    public String getUpdateUrl() {
        return downloadUrl;
    }

    @Override
    public void setUpdateUrl(String url) {
        this.downloadUrl = url;
    }
}