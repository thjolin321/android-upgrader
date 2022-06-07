package com.thjolin.update.check;

import com.thjolin.update.bean.ApkUpdateBean;
import com.thjolin.update.operate.flow.Flow;

/**
 * Created by th on 2021/5/24
 */
public interface CheckUpdateInterface extends Flow {

    int checkUpdateByHistoryVersions(ApkUpdateBean apkUpdateBean);

}
