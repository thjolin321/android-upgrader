package com.thjolin.download.task;

import android.text.TextUtils;

import com.thjolin.download.constant.Status;
import com.thjolin.download.database.DownloadEntity;
import com.thjolin.download.database.download.DownloadDaoFatory;
import com.thjolin.download.dispatcher.TaskDispatcher;
import com.thjolin.download.listener.DownloadListener;
import com.thjolin.download.listener.DownloadListenerWithSpeed;
import com.thjolin.download.task.speed.SpeedAssist;
import com.thjolin.util.FileHelper;
import com.thjolin.util.Logl;
import com.thjolin.util.Utils;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
    boolean needMoveToMainThread;
    boolean forceRepeat;
    int blockSize;
    long totalSize;
    long cacheSize;
    int progressDivide;
    Status status;
    InputStream body;


    // 一个task对应一个进度控制器
    ProgressController progressController;

    DownloadListener downloadListener;

    TaskCall taskCall;

    List<DownloadEntity> infoList;

    List<DownloadCall> callList;

    int downloadFinishSize;

    private DownloadTask(String url, String newFileMd5, String fileParent, String fileName,
                         boolean needProgress, boolean needSpeed,
                         boolean forceRepeat, boolean needMoveToMainThread,
                         int blockSize, int progressDivide) {
        this.url = url;
        this.newFileMd5 = newFileMd5;
        this.fileParent = fileParent;
        this.fileName = fileName;
        this.needProgress = needProgress;
        this.needSpeed = needSpeed;
        this.forceRepeat = forceRepeat;
        this.needMoveToMainThread = needMoveToMainThread;
        this.blockSize = blockSize;
        this.progressDivide = progressDivide;
        init();
    }

    private void init() {
        status = new Status();
    }


    private final class ProgressController {

        final AtomicLong sofarBytes;
        AtomicInteger tempProgress;
        SpeedAssist speedAssist;

        public ProgressController() {
            this.sofarBytes = new AtomicLong(cacheSize);
            this.tempProgress = new AtomicInteger((int) (cacheSize * 100 / totalSize));
            if (needProgress) {
                speedAssist = new SpeedAssist();
            }
        }

        public void progress(long progress) {
            long sofar = sofarBytes.addAndGet(progress);
            int finalProgress = (int) (sofar * 100 / totalSize);
            if (isNeedSpeed()) {
                speedAssist.downloading(progress);
            }
            if (finalProgress <= tempProgress.get()) {
                return;
            }
            tempProgress.set(finalProgress);
            if (tempProgress.get() % progressDivide != 0) {
                return;
            }
            if (isNeedSpeed()) {
                dealProgressListener(finalProgress, speedAssist.speed());
            } else {
                dealProgressListener(finalProgress);
            }
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
        boolean needMoveToMainThread = true;
        int blockSize;
        int progressDivide = 1;

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

        public Builder needMoveToMainThread(boolean needMoveToMainThread) {
            this.needMoveToMainThread = needMoveToMainThread;
            return this;
        }

        public Builder progressDivide(int progressDivide) {
            this.progressDivide = progressDivide;
            return this;
        }

        public Builder blockSize(int blockSize) {
            if (blockSize > 5) blockSize = 5;
            this.blockSize = blockSize;
            return this;
        }


        public DownloadTask build() {
            return new DownloadTask(url, newFileMd5, fileParent, fileName,
                    needProgress, needSpeed, forceRepeat, needMoveToMainThread, blockSize, progressDivide);
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

    public String getFileNameForce() {
        if (fileName == null) {
            return Utils.getFileNameFromUrl(url);
        }
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
        return needSpeed && downloadListener != null && downloadListener instanceof DownloadListenerWithSpeed;
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

    public List<DownloadEntity> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<DownloadEntity> infoList) {
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
        Logl.e("task forceDelete");
        setForceRepeat(true);
        setCacheSize(0);
        FileHelper.delete(FileHelper.getTargetFilePath(getFileParent(), getFileName()));
        FileHelper.createNewFile(FileHelper.getTargetFilePath(getFileParent(), getFileName()));
        DownloadDaoFatory.getDao().deleteByUrl(getUrl());
    }

    public void restart() {
        Logl.e("restart");
        setStatus(Status.START);
        TaskDispatcher.getInstance().start(this, downloadListener);
    }

    public synchronized void cancel() {
        if (status.getCode() == Status.DOWN) {
            return;
        }
        Logl.e("task cancel");
        downloadFinishSize = 0;
        cancel(Status.CANEL);
    }

    public synchronized void cancel(String msg) {
        if (taskCall != null) {
            taskCall.cancel();
        }
        if (callList != null) {
            for (DownloadCall call : callList) {
                call.cancel();
            }
        }
        TaskDispatcher.getInstance().removeTask(url);
        if (status.getCode() == Status.ERRO) {
            return;
        }
        dealFailedListener(msg);
        setStatus(Status.ERRO);
    }


    public synchronized void dealFinishDownloadCall(int index) {
        Logl.e("callList.size: " + callList.size());
        downloadFinishSize++;
        Logl.e("downloadFinishSize: " + downloadFinishSize + "  blockSize: " + blockSize);
        if (callList == null || callList.isEmpty()) return;
        for (int i = 0; i < callList.size(); i++) {
            if (callList.get(i).index == index) {
                callList.remove(i);
                break;
            }
        }
        if (callList.isEmpty() && downloadFinishSize == blockSize) {
//            if (FileHelper.getTargetFilePath(fileParent, fileName).length() != totalSize) {
//                dealFailedListener(Status.DOWUNLOAD_ERROR);
//                forceDelete();
//                Logl.e("文件长度不一致");
//                return;
//            }
            Logl.e("newFileMD5:" + newFileMd5);
            Logl.e("now md5: " + FileHelper.fileMd5(FileHelper.getTargetFilePath(fileParent, fileName)));
            if (!TextUtils.isEmpty(newFileMd5) && !newFileMd5
                    .equals(FileHelper.fileMd5(FileHelper.getTargetFilePath(fileParent, fileName)))) {
                // md5与预定值不一样
                Logl.e("MD5不一致");
                dealFailedListener(Status.MD5_UNMATCH);
                return;
            }
            dealRealSuccess();
        }
    }

    public void dealRealSuccess() {
        dealSuccessListener(FileHelper.getTargetFilePath(fileParent, fileName));
        TaskDispatcher.getInstance().removeTask(url);
        DownloadDaoFatory.getDao().deleteByUrl(url);
    }

    public void dealSuccessListener(String path) {
        if (needMoveToMainThread) {
            TaskDispatcher.getInstance().moveToMainThread(new Runnable() {
                @Override
                public void run() {
                    if (isNeedSpeed()) {
                        ((DownloadListenerWithSpeed) downloadListener)
                                .speed("0.0 kB/s");
                    }
                    downloadListener.progress(100);
                    downloadListener.success(path);
                }
            });
        } else {
            if (isNeedSpeed()) {
                ((DownloadListenerWithSpeed) downloadListener)
                        .speed("0.0 kB/s");
            }
            downloadListener.success(path);
        }
    }

    public void dealFailedListener(String msg) {
        if (downloadListener == null) return;
        if (needMoveToMainThread) {
            TaskDispatcher.getInstance().moveToMainThread(new Runnable() {
                @Override
                public void run() {
                    if (isNeedSpeed()) {
                        ((DownloadListenerWithSpeed) downloadListener)
                                .speed("0.0 kB/s");
                    }
                    downloadListener.failed(msg);
                }
            });
        } else {
            if (isNeedSpeed()) {
                ((DownloadListenerWithSpeed) downloadListener)
                        .speed("0.0 kB/s");
            }
            downloadListener.failed(msg);
        }
    }

    private void dealProgressListener(final int pro) {
        if (downloadListener == null) return;
        if (needMoveToMainThread) {
            TaskDispatcher.getInstance().moveToMainThread(new Runnable() {
                @Override
                public void run() {
                    downloadListener.progress(pro);
                }
            });
        } else {
            downloadListener.progress(pro);
        }
    }

    private void dealProgressListener(final int pro, String speed) {
        if (downloadListener == null) return;
        if (!(downloadListener instanceof DownloadListenerWithSpeed)) {
            dealProgressListener(pro);
            return;
        }
        if (needMoveToMainThread) {
            TaskDispatcher.getInstance().moveToMainThread(new Runnable() {
                @Override
                public void run() {
                    downloadListener.progress(pro);
                    ((DownloadListenerWithSpeed) downloadListener).speed(speed);
                }
            });
        } else {
            downloadListener.progress(pro);
            ((DownloadListenerWithSpeed) downloadListener).speed(speed);
        }
    }
}