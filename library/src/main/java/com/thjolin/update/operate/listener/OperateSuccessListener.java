package com.thjolin.update.operate.listener;

/**
 * Created by tanghao on 2021/5/24
 */
public interface OperateSuccessListener {

    void success(String msg);

    void failed(String msg);

    void progress(int progress);

}