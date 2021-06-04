package com.example.manager.task.wrapper;

import com.example.manager.task.DownloadTask;

/**
 * Created by tanghao on 2021/6/1
 */
public class DownloadConfiger implements DownloadTask.IDownloadConfiger {

    private DownloadTask.Builder builder;

    public DownloadConfiger(){
        builder = new DownloadTask.Builder();
    }

    @Override
    public DownloadTask.IDownloadConfiger url(String url) {
        return builder.url(url);
    }

    @Override
    public DownloadTask.IDownloadConfiger fileParent(String fileParent) {
        return builder.fileParent(fileParent);
    }

    @Override
    public DownloadTask.IDownloadConfiger fileName(String fileName) {
        return builder.fileName(fileName);
    }

    @Override
    public DownloadTask.IDownloadConfiger needProgress(boolean needProgress) {
        return builder.needProgress(needProgress);
    }

    @Override
    public DownloadTask.IDownloadConfiger needSpeed(boolean needSpeed) {
        return builder.needSpeed(needSpeed);
    }

    @Override
    public DownloadTask.IDownloadConfiger forceRepeat(boolean forceRepeat) {
        return builder.forceRepeat(forceRepeat);
    }

    @Override
    public DownloadTask.IDownloadConfiger blockSize(int blockSize) {
        return builder.blockSize(blockSize);
    }

    public DownloadTask createDownloadTask(){
        return builder.build();
    }

}