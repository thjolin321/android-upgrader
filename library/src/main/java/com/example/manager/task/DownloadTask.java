package com.example.manager.task;

import android.text.TextUtils;

import com.example.manager.constant.Status;
import com.example.manager.database.download.DownloadDaoFatory;
import com.example.manager.dispatcher.TaskDispatcher;
import com.example.manager.listener.DownloadListener;
import com.example.manager.util.FileUtils;
import com.example.manager.util.Logl;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by tanghao on 2021/5/27
 */
public class DownloadTask {

    String url;
    String newFileMd5;
    String fileParent;
    String fileName;
    boolean needProgress;
    boolean needSpeed;
    boolean forceRepeat;
    int blockSize;
    long totalSize;
    long cacheSize;
    Status status;
    InputStream body;

    // 一个task对应一个进度控制器
    ProgressController progressController;

    DownloadListener downloadListener;

    TaskCall taskCall;

    List<DownloadInfo> infoList;

    List<DownloadCall> callList;

    private DownloadTask(String url, String newFileMd5, String fileParent, String fileName,
                         boolean needProgress, boolean needSpeed, boolean forceRepeat, int blockSize) {
        this.url = url;
        this.newFileMd5 = newFileMd5;
        this.fileParent = fileParent;
        this.fileName = fileName;
        this.needProgress = needProgress;
        this.needSpeed = needSpeed;
        this.forceRepeat = forceRepeat;
        this.blockSize = blockSize;
        init();
    }

    private void init() {
        status = new Status();
    }


    private final class ProgressController {

        final AtomicLong sofarBytes;
        volatile long tempProgress;


        public ProgressController() {
            this.sofarBytes = new AtomicLong(cacheSize);
        }

        public void progress(long progress) {
            long sofar = sofarBytes.addAndGet(progress);
            int finalProgress = (int) (sofar / (float) totalSize * 100);
            if (finalProgress <= tempProgress) {
                return;
            }
            tempProgress = finalProgress;
            downloadListener.progress(finalProgress);
        }

    }

    public static class Builder {

        String url;
        String newFileMd5;
        String fileParent;
        String fileName;
        boolean needProgress;
        boolean needSpeed;
        boolean forceRepeat;
        int blockSize;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder newFileMd5(String newFileMd5) {
            this.newFileMd5 = newFileMd5;
            return this;
        }


        public Builder fileParent(String fileParent) {
            this.fileParent = fileParent;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder needProgress(boolean needProgress) {
            this.needProgress = needProgress;
            return this;
        }

        public Builder needSpeed(boolean needSpeed) {
            this.needSpeed = needSpeed;
            return this;
        }

        public Builder forceRepeat(boolean forceRepeat) {
            this.forceRepeat = forceRepeat;
            return this;
        }

        public Builder blockSize(int blockSize) {
            if (blockSize > 5) blockSize = 5;
            this.blockSize = blockSize;
            return this;
        }

        public DownloadTask build() {
            return new DownloadTask(url, newFileMd5, fileParent, fileName,
                    needProgress, needSpeed, forceRepeat, blockSize);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileParent() {
        return fileParent;
    }

    public void setFileParent(String fileParent) {
        this.fileParent = fileParent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isNeedProgress() {
        return needProgress;
    }

    public void setNeedProgress(boolean needProgress) {
        this.needProgress = needProgress;
    }

    public boolean isNeedSpeed() {
        return needSpeed;
    }

    public void setNeedSpeed(boolean needSpeed) {
        this.needSpeed = needSpeed;
    }

    public boolean forceRepeat() {
        return forceRepeat;
    }

    public void setForceRepeat(boolean forceRepeat) {
        this.forceRepeat = forceRepeat;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(int statusCode) {
        if (status != null) {
            this.status.setStatus(statusCode);
        }
    }

    public List<DownloadInfo> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<DownloadInfo> infoList) {
        this.infoList = infoList;
    }

    public List<DownloadCall> getCallList() {
        return callList;
    }

    public void setCallList(List<DownloadCall> callList) {
        this.callList = callList;
    }

    public String getNewFileMd5() {
        return newFileMd5;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void dealProgress(long progress) {
        if (progressController == null) {
            return;
        }
        progressController.progress(progress);
    }

    public void createProgressController() {
        this.progressController = new ProgressController();
    }

    public DownloadListener getDownloadListener() {
        return downloadListener;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public InputStream getBody() {
        return body;
    }

    public void setBody(InputStream body) {
        this.body = body;
    }

    public void setTaskCall(TaskCall taskCall) {
        this.taskCall = taskCall;
    }

    public void forceDelete() {
        setForceRepeat(true);
        setCacheSize(0);
        FileUtils.delete(FileUtils.getTargetFilePath(getFileParent(), getFileName()));
        FileUtils.createNewFile(FileUtils.getTargetFilePath(getFileParent(), getFileName()));
        DownloadDaoFatory.getDao().deleteByUrl(getUrl());
    }

    public synchronized void cancel() {
        cancel(Status.CANEL);
    }

    public synchronized void cancel(String msg) {
        if (status.getCode() == Status.ERRO) {
            return;
        }
        if (taskCall != null) {
            taskCall.cancel();
        }
        if (callList != null) {
            for (DownloadCall call : callList) {
                call.cancel();
            }
        }
        TaskDispatcher.getInstance().removeTask(url);
        downloadListener.failed(msg);
        setStatus(Status.ERRO);
    }


    public synchronized void dealFinishDownloadCall(int index) {
        Logl.e("callList.size: " + callList.size());
        if (callList == null || callList.isEmpty()) return;
        for (int i = 0; i < callList.size(); i++) {
            if (callList.get(i).index == index) {
                callList.remove(i);
                break;
            }
        }
        if (callList.isEmpty()) {
            downloadListener.progress(100);
            if (!TextUtils.isEmpty(newFileMd5) && !newFileMd5
                    .equals(FileUtils.fileMd5(FileUtils.getTargetFilePath(fileParent, fileName)))) {
                // md5与预定值不一样
                downloadListener.failed(Status.MD5_UNMATCH);
                return;
            }
            downloadListener.success(FileUtils.getTargetFilePath(fileParent, fileName));
            DownloadDaoFatory.getDao().deleteByUrl(url);
        }
    }

    @Override
    public String toString() {
        return "DownloadTask{" +
                "url='" + url + '\'' +
                ", newFileMd5='" + newFileMd5 + '\'' +
                ", fileParent='" + fileParent + '\'' +
                ", fileName='" + fileName + '\'' +
                ", needProgress=" + needProgress +
                ", needSpeed=" + needSpeed +
                ", forceRepeat=" + forceRepeat +
                ", blockSize=" + blockSize +
                ", totalSize=" + totalSize +
                ", cacheSize=" + cacheSize +
                ", status=" + status +
                ", progressController=" + progressController +
                ", downloadListener=" + downloadListener +
                ", infoList=" + infoList +
                ", callList=" + callList +
                '}';
    }
}