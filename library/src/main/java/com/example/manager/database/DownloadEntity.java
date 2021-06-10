package com.example.manager.database;

import com.example.manager.database.base.BaseDO;
import com.example.manager.database.base.BaseDb;

public class DownloadEntity extends BaseDO {

    // 开始下载的位置
    private long start;

    // url标志位
    private String url;

    // 当前线程id
    private int threadId;

    // 已下载的长度
    private long progress;

    // 需要下载的总长度
    private long contentLength;

    public DownloadEntity(long start, String url, int threadId, long progress, long contentLength) {
        this.start = start;
        this.url = url;
        this.threadId = threadId;
        this.progress = progress;
        this.contentLength = contentLength;
    }

    public DownloadEntity(){
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStart() {
        return start;
    }



    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public String toString() {
        return "DownloadEntity{" +
                "start=" + start +
                ", url='" + url + '\'' +
                ", threadId=" + threadId +
                ", progress=" + progress +
                ", contentLength=" + contentLength +
                '}';
    }
}
