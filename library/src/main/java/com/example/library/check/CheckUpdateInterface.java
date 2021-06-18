package com.example.library.check;

import com.example.library.bean.ApkPatchBean;
import com.example.library.bean.ApkUpdateBean;
import com.example.library.operate.flow.Flow;

import java.util.List;

/**
 * Created by tanghao on 2021/5/24
 */
public interface CheckUpdateInterface extends Flow {

    int checkUpdateByHistoryVersions(ApkUpdateBean apkUpdateBean);

}
