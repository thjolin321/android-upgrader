package com.thjolin.download.task.interceptor;

import com.thjolin.download.dispatcher.TaskDispatcher;
import com.thjolin.download.task.DownloadCall;
import com.thjolin.download.task.DownloadTask;
import com.thjolin.util.FileHelper;
import com.thjolin.util.Logl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghao on 2021/6/1
 */
public class DownloadInterceptor extends AbstractIntercepter implements TaskInterceptor {
    @Override
    public DownloadTask operate(DownloadTask task) {
        Logl.e("开始执行DownloadInterceptor");
        createDownloadCall(task);
        return task;
    }


    private void createDownloadCall(DownloadTask task) {
        FileHelper.createNewFile(FileHelper.getTargetFilePath(task.getFileParent(), task.getFileName()));
        if (!FileHelper.isFileExist(FileHelper.getTargetFilePath(task.getFileParent(), task.getFileName()))) {
            task.cancel("创建文件失败");
            return;
        }
        List<DownloadCall> list = new ArrayList<>(task.getInfoList().size());
        task.setCallList(list);
        Logl.e("task.geInfoList: " + task.getInfoList().size());
        for (int i = 0; i < task.getInfoList().size(); i++) {
            DownloadCall downloadCall = new DownloadCall(DownloadCall.class.getName() + i, task, i);
            list.add(downloadCall);
            Logl.e("添加次数: " + task.getInfoList().get(i).getStart());
        }
        for (DownloadCall call : list) {
            TaskDispatcher.getInstance().getmExecutorService().submit(call);
        }
    }

}