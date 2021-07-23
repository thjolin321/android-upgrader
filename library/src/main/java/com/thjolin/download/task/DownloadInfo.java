package com.thjolin.download.task;

import androidx.annotation.IntRange;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by tanghao on 2021/5/26
 */
public class DownloadInfo {

    private long id;

    @IntRange(from = 0)
    private final long start;
    @IntRange(from = 0)
    private final long contentLength;

    private final AtomicLong progress;

    public DownloadInfo(long start, long contentLength) {
        this(0, start, contentLength);
    }

    public DownloadInfo(long id, long start, long contentLength) {
        this(id, start, contentLength, 0);
    }

    public DownloadInfo(long id, long start, long contentLength, long progress) {
        this.id = id;
        this.contentLength = contentLength;
        this.start = start;
        this.progress = new AtomicLong(progress);
    }

    public long getStart() {
        return start;
    }

    public long getContentLength() {
        return contentLength;
    }

    public long getProgress() {
        return progress.get();
    }

    public void addProgress(long buffer) {
        this.progress.addAndGet(buffer);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "startOffset=" + start +
                ", contentLength=" + contentLength +
                ", progress=" + progress +
                '}';
    }
}