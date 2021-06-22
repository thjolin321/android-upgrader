package com.example.manager.task;


import android.util.Log;

import com.example.manager.constant.Status;
import com.example.manager.database.DownloadEntity;
import com.example.manager.database.download.DownloadDaoFatory;
import com.example.manager.http.HttpUtil;
import com.example.manager.util.DownloadUtils;
import com.example.manager.util.Logl;
import com.example.manager.util.NamedRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Objects;

import okhttp3.Response;

/**
 * Created by tanghao on 2021/5/27
 */
public class DownloadCall extends NamedRunnable {

    private final static int RETRY_COUNT = 5;
    int retry = 0;
    DownloadInfo downloadInfo;
    DownloadTask task;
    int index;

    public DownloadCall(String name, DownloadTask task, int index) {
        super(name);
        this.task = task;
        this.downloadInfo = task.getInfoList().get(index);
        this.index = index;
    }

    @Override
    protected void execute() {
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            if (task.getBlockSize() == 1 && task.getBody() != null) {
                inputStream = task.getBody();
            } else {
                Response response = HttpUtil.with().syncResponse(task.getUrl(),
                        downloadInfo.getStartOffset() + downloadInfo.getProgress(),
                        downloadInfo.getStartOffset() + downloadInfo.getContentLength());
                inputStream = Objects.requireNonNull(response.body()).byteStream();
            }
            //保存文件的路径
            File file = new File(task.getFileParent(), task.getFileName());
            randomAccessFile = new RandomAccessFile(file, "rwd");
            //seek从哪里开始
            randomAccessFile.seek(downloadInfo.getStartOffset() + downloadInfo.getProgress());
            int length;
            byte[] bytes = new byte[10 * 1024];
            while ((length = inputStream.read(bytes)) != -1) {
                //写入
                randomAccessFile.write(bytes, 0, length);
                task.dealProgress(length);
                downloadInfo.addProgress(length);
            }
            setFinished(true);
            Logl.e("isFinish(): "+isFinished());
        } catch (Exception e) {
            Logl.e("IOException: " + e.getMessage());
            if ("interrupted".equals(e.getMessage())) {
                return;
            }
            if (retry++ < RETRY_COUNT) {
                DownloadUtils.close(inputStream);
                DownloadUtils.close(randomAccessFile);
                execute();
            } else {
                task.cancel(Status.DOWUNLOAD_ERROR);
            }
        } finally {
            DownloadUtils.close(inputStream);
            DownloadUtils.close(randomAccessFile);
            //保存到数据库
            saveToDb();
            Logl.e("isFinish(): "+isFinished());
            if(isFinished()){
                task.dealFinishDownloadCall(index);
            }
        }
    }

    private void saveToDb() {
        Log.e("TAG", "**************保存到数据库*******************");
        DownloadEntity entity = new DownloadEntity();
        entity.setProgress(downloadInfo.getProgress());
        entity.setUrl(task.getUrl());
        entity.setStart(downloadInfo.getStartOffset());
        entity.setContentLength(downloadInfo.getContentLength());
        entity.setThreadId(index);
        entity.setId(downloadInfo.getId());
        //保存到数据库
        Logl.e("插入数据库:" + DownloadDaoFatory.getDao().insertOrUpdate(entity) + "");
    }

    public void cancel() {
        if (getmCurrentThread() != null) {
            getmCurrentThread().interrupt();
        } else {
            setFinished(true);
        }
    }

    @Override
    protected void interrupted(InterruptedException e) {
        Logl.e("InterruptedException: " + e.getMessage());
    }

    @Override
    public String toString() {
        return "DownloadCall{" +
                "retry=" + retry +
                ", downloadInfo=" + downloadInfo +
                ", task=" + task +
                ", index=" + index +
                '}';
    }
}