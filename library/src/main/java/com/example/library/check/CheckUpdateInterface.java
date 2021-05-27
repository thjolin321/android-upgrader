package com.example.library.check;

import com.example.library.bean.ApkPatchBean;
import com.example.library.bean.ApkUpdateBean;

import java.util.List;

/**
 * Created by tanghao on 2021/5/24
 */
public interface CheckUpdateInterface {

    int checkUpdateByHistoryVersions(ApkUpdateBean apkUpdateBean);

    String getUpdateUrl();

    void setUpdateUrl(String url);

}
