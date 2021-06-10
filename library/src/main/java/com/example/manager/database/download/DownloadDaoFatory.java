package com.example.manager.database.download;

import com.example.manager.database.DownloadEntity;
import com.example.manager.database.base.DaoFactory;

import java.security.Permission;

/**
 * Created by tanghao on 2021/6/9
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