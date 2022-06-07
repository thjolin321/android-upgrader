package com.thjolin.download.database.download;

import com.thjolin.download.database.DownloadEntity;
import com.thjolin.download.database.base.BaseDb;

import java.util.List;

/**
 * Created by th on 2021/6/8
 */
public interface DaoDownload extends BaseDb<DownloadEntity> {

    long qureyAllCacheSize(String url);

    List<DownloadEntity> qureyAllByUrl(String url);

    long deleteByUrl(String url);

}
