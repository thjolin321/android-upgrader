package com.example.manager.task;

import android.text.TextUtils;

import com.example.manager.constant.Status;

import java.util.List;

/**
 * Created by tanghao on 2021/5/27
 */
public class DownloadTask {

    String url;
    String fileParent;
    String fileName;
    boolean needProgress;
    boolean needSpeed;
    boolean forceRepeat;
    int coreSize;

    long totalSize;

    Status status;

    private List<DownloadInfo> infoList;

    private List<DownloadCall> callList;

    public DownloadTask(String url, String fileParent, String fileName, boolean needProgress, boolean needSpeed, boolean forceRepeat, int coreSize) {
        this.url = url;
        this.fileParent = fileParent;
        this.fileName = fileName;
        this.needProgress = needProgress;
        this.needSpeed = needSpeed;
        this.forceRepeat = forceRepeat;
        this.coreSize = coreSize;
        init();
    }

    private void init() {
        status = new Status();
        if (TextUtils.isEmpty(url)) {
            status.msg = "CHECK_URL";
            status.status = -1;
        }
    }


    public static class Builder {

        String url;
        String fileParent;
        String fileName;
        boolean needProgress;
        boolean needSpeed;
        boolean forceRepeat;
        int coreSize;

        public Builder setUrl(String url) {
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

        public Builder coreSize(int coreSize) {
            this.coreSize = coreSize;
            return this;
        }

        public DownloadTask build() {
            return new DownloadTask(url, fileParent, fileName,
                    needProgress, needSpeed, forceRepeat, coreSize);
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

    public boolean isForceRepeat() {
        return forceRepeat;
    }

    public void setForceRepeat(boolean forceRepeat) {
        this.forceRepeat = forceRepeat;
    }

    public int getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
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

    public void setStatus(Status status) {
        this.status = status;
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
}