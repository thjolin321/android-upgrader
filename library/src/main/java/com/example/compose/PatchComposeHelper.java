package com.example.compose;

import androidx.annotation.NonNull;
import com.example.manager.database.DownloadProvider;
import com.example.manager.util.FileUtils;
import com.example.manager.util.Logl;


/**
 * Created by tanghao on 2021/6/15
        */
public class PatchComposeHelper {

    public static int patch(@NonNull ComposeTask task) {
        int result = BsPatchUtils.patch(DownloadProvider.context.getApplicationInfo().sourceDir, task.getNewFilePath(),
                task.getPatchPath());
        Logl.e("组装result: " + result);
        if (result == 0 && task.getCompleteApkMd5() != null) {
            String patchApkMd5 = FileUtils.fileMd5(task.getNewFilePath());
            if (!task.getCompleteApkMd5().equals(patchApkMd5)) {
                FileUtils.delete(task.getNewFilePath());
                return -20;
            }
        }
        return 0;
    }
}