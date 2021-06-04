package com.example.manager.task;


import com.example.manager.http.HttpUtil;
import com.example.manager.util.DownloadUtils;
import com.example.manager.util.NamedRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;

/**
 * Created by tanghao on 2021/5/27
 */
public class DownloadCall extends NamedRunnable {

    DownloadInfo downloadInfo;
    DownloadTask task;

    public DownloadCall(String name, DownloadTask task, int index) {
        super(name);
        this.task = task;
        this.downloadInfo = task.getInfoList().get(index);
    }

    @Override
    protected void execute() throws InterruptedException {
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            Response response = HttpUtil.with().syncResponse(task.getUrl(), downloadInfo.getStartOffset(), downloadInfo.getContentLength());
            inputStream = response.body().byteStream();
            //保存文件的路径
            File file = new File(task.getFileParent(), task.getFileName());
            randomAccessFile = new RandomAccessFile(file, "rwd");
            //seek从哪里开始
            randomAccessFile.seek(downloadInfo.getStartOffset());
            int length;
            byte[] bytes = new byte[10 * 1024];
            while ((length = inputStream.read(bytes)) != -1) {
                //写入
                randomAccessFile.write(bytes, 0, length);
                task.dealProgress(length);
            }
        } catch (IOException e) {
            e.printStackTrace();
//            downloadCallback.onFailure(e);
        } finally {
            DownloadUtils.close(inputStream);
            DownloadUtils.close(randomAccessFile);
//            //保存到数据库
//            saveToDb();
        }

    }

    @Override
    protected void interrupted(InterruptedException e) {

    }

    @Override
    protected void finished() {

    }
}