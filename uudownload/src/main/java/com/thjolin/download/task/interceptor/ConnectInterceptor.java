package com.thjolin.download.task.interceptor;

import com.thjolin.download.UuDownloader;
import com.thjolin.download.constant.Status;
import com.thjolin.download.task.DownloadTask;
import com.thjolin.download.util.Logl;

import java.util.Objects;

import okhttp3.Response;

/**
 * Created by tanghao on 2021/5/31
 */
public class ConnectInterceptor extends AbstractInterceptor implements TaskInterceptor {

    @Override
    public DownloadTask operate(DownloadTask task) {
        Response response = null;
        try {
            response = UuDownloader.with().getHttpUtil().asyncCall(task.getUrl());
        } catch (Exception e) {
            Logl.e("ConnectIntercepter: 链接连接问题：" + e.getMessage());
            task.setStatus(Status.ERRO);
            e.printStackTrace();
        }
        if (response == null) {
            Logl.e("链接出错，response为空");
            task.setStatus(Status.ERRO);
            task.getStatus().setMsg(Status.CHECK_URL);
            return task;
        }
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