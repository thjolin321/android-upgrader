package com.example.manager.task;

import androidx.annotation.IntRange;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by tanghao on 2021/5/26
 */
public class DownloadInfo {

    private long id;

    @IntRange(from = 0)
    private final long startOffset;
    @IntRange(from = 0)
    private final long contentLength;

    private final AtomicLong progress;

    public DownloadInfo(long startOffset, long contentLength) {
        this(0, startOffset, contentLength);
    }

    public DownloadInfo(long id, long startOffset, long contentLength) {
        this(id, startOffset, contentLength, 0);
    }

    public DownloadInfo(long id, long startOffset, long contentLength, long progress) {
        this.id = id;
        this.contentLength = contentLength;
        this.startOffset = startOffset;
        this.progress = new AtomicLong(progress);
    }

    public long getStartOffset() {
        return startOffset;
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
                "startOffset=" + startOffset +
                ", contentLength=" + contentLength +
                ", progress=" + progress +
                '}';
    }
}