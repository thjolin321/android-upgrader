package com.thjolin.download.task.interceptor;

import com.thjolin.download.DownloadManager;
import com.thjolin.download.constant.Status;
import com.thjolin.download.task.DownloadTask;
import com.thjolin.util.Logl;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Response;

/**
 * Created by tanghao on 2021/5/31
 */
public class ConnectIntercepter extends AbstractIntercepter implements TaskInterceptor {

    @Override
    public DownloadTask operate(DownloadTask task) {
        Response response = null;
        try {
            response = DownloadManager.with().getHttpUtil().asyncCall(task.getUrl());
        } catch (IOException e) {
            task.setStatus(Status.ERRO);
            e.printStackTrace();
        }
        assert response != null;
        long totalSize = Objects.requireNonNull(response.body()).contentLength();
        task.setBody(Objects.requireNonNull(response.body()).byteStream());
        Logl.e("返回totalSize: " + totalSize);
        if (totalSize <= 0) {
            task.setStatus(Status.ERRO);
            task.getStatus().setMsg(Status.CHECK_URL);
        }
        task.setTotalSize(totalSize);
        if (task.getTotalSize() == task.getCacheSize()) {
            task.setStatus(Status.DOWN);
        }
        return task;
    }
}