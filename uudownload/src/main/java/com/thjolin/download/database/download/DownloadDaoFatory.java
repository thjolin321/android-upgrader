package com.thjolin.download.database.download;

import com.thjolin.download.database.DownloadEntity;
import com.thjolin.download.database.base.DaoFactory;

/**
 * Created by th on 2021/6/9
 */
public class DownloadDaoFatory extends DaoFactory {
    static DaoDownload daoDownload;

    static {
        daoDownload = new DaoDownloadImpl();
        daoDownload.init(getFactory().getmSqLiteDatabase(), DownloadEntity.class);
    }

    public static DaoDownload getDao() {
        return daoDownload;
    }
}