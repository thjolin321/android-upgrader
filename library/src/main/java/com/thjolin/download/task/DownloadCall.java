package com.thjolin.download.task;

import android.util.Log;

import com.thjolin.download.constant.Status;
import com.thjolin.download.database.DownloadEntity;
import com.thjolin.download.database.download.DownloadDaoFatory;
import com.thjolin.download.http.HttpUtil;
import com.thjolin.util.Utils;
import com.thjolin.util.Logl;
import com.thjolin.util.NamedRunnable;

import java.io.File;
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
                        downloadInfo.getStart() + downloadInfo.getProgress(),
                        downloadInfo.getStart() + downloadInfo.getContentLength());
                Logl.e("response获取成功：" + Thread.currentThread().getName() + "==" + response.body().contentLength());
                inputStream = Objects.requireNonNull(response.body()).byteStream();
            }
            //保存文件的路径
            File file = new File(task.getFileParent(), task.getFileName());
            randomAccessFile = new RandomAccessFile(file, "rwd");
            //seek从哪里开始
            randomAccessFile.seek(downloadInfo.getStart() + downloadInfo.getProgress());
            Logl.e("seek成功：");
            int length;
            int countDb = 0;
            byte[] bytes = new byte[10 * 1024];
            while ((length = inputStream.read(bytes)) != -1) {
                //写入
                randomAccessFile.write(bytes, 0, length);
                task.dealProgress(length);
                downloadInfo.addProgress(length);
//                countDb++;
//                if (countDb % 1024 == 0) {
//                    saveToDb();
//                }
            }
            Logl.e("写入完成: " + bytes.length);
            setFinished(true);
            Logl.e("isFinish(): " + isFinished());
        } catch (Exception e) {
            Logl.e("IOException: " + e.getMessage());
            if ("interrupted".equals(e.getMessage())) {
                return;
            }
            if (retry++ < RETRY_COUNT) {
                Utils.close(inputStream);
                Utils.close(randomAccessFile);
                execute();
            } else {
                task.cancel(Status.DOWUNLOAD_ERROR);
            }
        } finally {
            Utils.close(inputStream);
            Utils.close(randomAccessFile);
            //保存到数据库
            saveToDb();
            Logl.e("isFinish(): " + isFinished());
            if (isFinished()) {
                task.dealFinishDownloadCall(index);
            }
        }
    }

    private void saveToDb() {
        Log.e("TAG", "**************保存到数据库*******************");
        DownloadEntity entity = new DownloadEntity();
        entity.setProgress(downloadInfo.getProgress());
        entity.setUrl(task.getUrl());
        entity.setStart(downloadInfo.getStart());
        entity.setContentLength(downloadInfo.getContentLength());
        entity.setThreadId(index);
        entity.setId(downloadInfo.getId());
        //保存到数据库
        Logl.e("当前插入：entity");
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
}