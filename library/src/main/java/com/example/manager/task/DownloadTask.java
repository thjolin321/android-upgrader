package com.example.manager.task;

import android.text.TextUtils;

import com.example.manager.constant.Status;
import com.example.manager.listener.DownloadListener;

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

    // 一个task对应一个进度控制器
    private ProgressController progressController;

    private DownloadListener downloadListener;

    private List<DownloadInfo> infoList;

    private List<DownloadCall> callList;

    public DownloadTask(String url, String fileParent, String fileName, boolean needProgress, boolean needSpeed, boolean forceRepeat, int blockSize) {
        this.url = url;
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

        public ProgressController() {
            this.sofarBytes = new AtomicLong(0);
        }

        public void progress(long progress) {
            long sofar = sofarBytes.addAndGet(progress);
            downloadListener.progress((int) (sofar / totalSize * 100));
        }

    }

    public static class Builder implements IDownloadConfiger {

        String url;
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
            this.blockSize = blockSize;
            return this;
        }

        public DownloadTask build() {
            return new DownloadTask(url, fileParent, fileName,
                    needProgress, needSpeed, forceRepeat, blockSize);
        }
    }

    public interface IDownloadConfiger {

        IDownloadConfiger url(String url);

        IDownloadConfiger fileParent(String fileParent);

        IDownloadConfiger fileName(String fileName);

        IDownloadConfiger needProgress(boolean needProgress);

        IDownloadConfiger needSpeed(boolean needSpeed);

        IDownloadConfiger forceRepeat(boolean forceRepeat);

        IDownloadConfiger blockSize(int blockSize);

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
}