package com.thjolin.update.check;

import android.text.TextUtils;

import com.thjolin.compose.ComposeTask;
import com.thjolin.update.bean.ApkPatchBean;
import com.thjolin.update.bean.ApkUpdateBean;
import com.thjolin.update.operate.flow.WorkFlow;
import com.thjolin.download.task.DownloadTask;

/**
 * Created by th on 2021/5/25
 */
public class ChekUpdateImpl implements CheckUpdateInterface {

    WorkFlow workFlow;

    DownloadTask.Builder builder;

    ApkUpdateBean apkUpdateBean;

    public ChekUpdateImpl() {
        workFlow = new WorkFlow();
        workFlow.setFlow(WorkFlow.FLOW_CHECK);
    }

    /**
     * 检查更新方式
     * @param apkUpdateBean
     * @return -1 for no need update, 1、for patch, 2 for completeApk, 3 for marcket, 4 for 插件化更新, -2 for wrong type
     */
    @Override
    public int checkUpdateByHistoryVersions(ApkUpdateBean apkUpdateBean) {
        this.apkUpdateBean = apkUpdateBean;
        if (apkUpdateBean.getCurrentApkVersionCode() >= apkUpdateBean.getNewApkVersionCode()) {
            return -1;
        }
        if (apkUpdateBean.getList() != null) {
            for (ApkPatchBean apkPatchBean : apkUpdateBean.getList()) {
                if (apkPatchBean.getVersionCode() == apkUpdateBean.getCurrentApkVersionCode()) {
                    if (builder == null) {
                        builder = new DownloadTask.Builder();
                    }
                    builder.url(apkPatchBean.getPatchUrl());
                    builder.newFileMd5(apkPatchBean.getPatchMd5());
                    workFlow.setComposeTask(new ComposeTask(builder.getFinalFilePath(), apkUpdateBean.getNewApkMd5()));
                    return 1;
                }
            }
        }
        if (TextUtils.isEmpty(apkUpdateBean.getNewApkUrl())) {
            builder = null;
            return -2;
        }
        if (builder == null) {
            builder = new DownloadTask.Builder();
        }
        builder.url(apkUpdateBean.getNewApkUrl());
        builder.newFileMd5(apkUpdateBean.getNewApkMd5());
        return 2;
    }


    @Override
    public WorkFlow getFlow() {
        if (builder != null) {
            workFlow.setDownloadTask(builder);
        }
        return workFlow;
    }

}