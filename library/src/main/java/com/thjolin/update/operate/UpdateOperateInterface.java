package com.thjolin.update.operate;

/**
 * Created by tanghao on 2021/5/24
 */
public interface UpdateOperateInterface {

    void updateApkByPatchPath(String patchString, String nowApkPath);

    void updateApkByCompleteUrl(String apkUrl);

    void downloadFile(String url);

}
