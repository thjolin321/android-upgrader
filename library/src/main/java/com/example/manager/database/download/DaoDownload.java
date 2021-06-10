package com.example.manager.database.download;

import android.util.Log;

import com.example.manager.database.DownloadEntity;
import com.example.manager.database.base.BaseDb;

import java.util.List;

/**
 * Created by tanghao on 2021/6/8
 */
public interface DaoDownload extends BaseDb<DownloadEntity> {

    long qureyAllCacheSize(String url);

    List<DownloadEntity> qureyAllByUrl(String url);

    long deleteByUrl(String url);

}
