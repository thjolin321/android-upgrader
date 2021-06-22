package com.example.library.operate.flow;

import com.example.compose.ComposeTask;
import com.example.manager.task.DownloadTask;

import java.io.File;

/**
 * Created by tanghao on 2021/6/15
 */
public class WorkFlow {

    public final static int FLOW_CHECK = 1;
    public final static int FLOW_DOWNLOAD = 2;
    public final static int FLOW_COMPOSE = 3;
    public final static int FLOW_INSTALL = 4;
    public final static int FLOW_MARKET = 5;

    private int flow;
    private DownloadTask downloadTask;
    private ComposeTask composeTask;

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public void setDownloadTask(DownloadTask downloadTask) {
        this.downloadTask = downloadTask;
    }

    public void setComposeTask(ComposeTask composeTask) {
        this.composeTask = composeTask;
    }

    public DownloadTask getDownloadTask() {
        return downloadTask;
    }

    public ComposeTask getComposeTask() {
        return composeTask;
    }
}