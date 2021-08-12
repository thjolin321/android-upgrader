package com.thjolin.download.task;

import android.util.Log;

import com.thjolin.download.constant.Status;
import com.thjolin.download.database.DownloadEntity;
import com.thjolin.download.database.download.DownloadDaoFatory;
import com.thjolin.download.http.HttpUtil;
import com.thjolin.download.util.Logl;
import com.thjolin.download.util.NamedRunnable;
import com.thjolin.download.util.Utils;

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
    DownloadEntity downloadEntity;
    DownloadTask task;
    int index;
    volatile boolean canceled;
    volatile boolean finishing;

    public DownloadCall(String name, DownloadTask task, int index) {
        super(name);
        this.task = task;
        this.downloadEntity = task.getInfoList().get(index);
        this.index = index;
    }

    @Override
    protected void execute() {
        Logl.e("DownloadCall: " + downloadEntity.getProgress());
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        if (downloadEntity.getProgress() == downloadEntity.getContentLength() || canceled || finishing) {
            return;
        }
        try {
            if (task.getBlockSize() == 1 && task.getBody() != null) {
                inputStream = task.getBody();
            } else {
                Response response = HttpUtil.with().syncResponse(task.getUrl(),
                        downloadEntity.getStart() + downloadEntity.getProgress(),
                        downloadEntity.getStart() + downloadEntity.getContentLength());
                inputStream = Objects.requireNonNull(response.body()).byteStream();
            }
            //保存文件的路径
            File file = new File(task.getFileParent(), task.getFileName());
            randomAccessFile = new RandomAccessFile(file, "rwd");
            //seek从哪里开始
            randomAccessFile.seek(downloadEntity.getStart() + downloadEntity.getProgress());
            int length;
            byte[] bytes = new byte[10 * 1024];
            while ((length = inputStream.read(bytes)) != -1) {
                if (canceled || finishing) {
                    return;
                }
                randomAccessFile.write(bytes, 0, length);
                task.dealProgress(length);
                downloadEntity.addProgress(length);
            }
            finishing = true;
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
                task.cancel(Status.DOWNLOAD_ERROR);
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
        entity.setProgress(downloadEntity.getProgress());
        entity.setUrl(task.getUrl());
        entity.setStart(downloadEntity.getStart());
        entity.setContentLength(downloadEntity.getContentLength());
        entity.setThreadId(index);
        entity.setId(downloadEntity.getId());
        //保存到数据库
        Logl.e("saveToDb: " + entity);
        Logl.e("插入数据库:" + DownloadDaoFatory.getDao().insertOrUpdate(downloadEntity) + "");
    }

    public synchronized void cancel() {
        canceled = true;
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