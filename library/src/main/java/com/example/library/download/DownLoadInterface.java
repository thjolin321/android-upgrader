package com.example.library.download;

import com.example.library.operate.OperateSuccessListener;

/**
 * Created by tanghao on 2021/5/24
 */
public interface DownLoadInterface {

    void downLoadFileWithRepeatTime(String fileUrl, String dirPath, String name, int repeatTime, OperateSuccessListener listener);

    void downLoadFileWithRepeatTime(String fileUrl, String completePath, int repeatTime, OperateSuccessListener listener);

    void downLoadFile(String fileUrl, String completePath, OperateSuccessListener listener);

}
