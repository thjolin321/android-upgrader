package com.example.library.download;

import com.example.library.operate.listener.OperateSuccessListener;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tanghao on 2021/5/25
 */
public class DownloadImpl implements DownLoadInterface {

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Override
    public void downLoadFileWithRepeatTime(String fileUrl, String dirPath, String name, int repeatTime, OperateSuccessListener listener) {

    }

    @Override
    public void downLoadFileWithRepeatTime(String fileUrl, String completePath, int repeatTime, OperateSuccessListener listener) {

    }

    @Override
    public void downLoadFile(String fileUrl, String completePath, OperateSuccessListener listener) {

    }
}