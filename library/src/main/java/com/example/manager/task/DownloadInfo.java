package com.example.manager.task;

import androidx.annotation.IntRange;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by tanghao on 2021/5/26
 */
public class DownloadInfo {

    @IntRange(from = 0)
    private final long startOffset;
    @IntRange(from = 0)
    private final long contentLength;

    private final AtomicLong currentOffset;

    public DownloadInfo(long startOffset, long contentLength) {
        this(startOffset, contentLength, 0);
    }

    public DownloadInfo(long startOffset, long contentLength, long currentOffset) {
        this.startOffset = startOffset;
        this.contentLength = contentLength;
        this.currentOffset = new AtomicLong(currentOffset);
    }

    public long getStartOffset() {
        return startOffset;
    }

    public long getContentLength() {
        return contentLength;
    }

    public AtomicLong getCurrentOffset() {
        return currentOffset;
    }

}