package com.thjolin.download.database.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.thjolin.download.database.DownloadProvider;

import java.io.File;


public class DaoFactory {

    private static DaoFactory mFactory;

    // 持有外部数据库的引用
    SQLiteDatabase mSqLiteDatabase;

    public DaoFactory() {
        if (mSqLiteDatabase == null) {
            init(DownloadProvider.context);
        }
    }

    public static DaoFactory getFactory() {
        if (mFactory == null) {
            synchronized (DaoFactory.class) {
                if (mFactory == null) {
                    mFactory = new DaoFactory();
                }
            }
        }
        return mFactory;
    }

    public <T extends BaseDO> BaseDb<T> getDao(Class<T> clazz) {
        BaseDb<T> daoSupport = new BaseDbImpl<T>();
        daoSupport.init(mSqLiteDatabase, clazz);
        return daoSupport;
    }

    public SQLiteDatabase getmSqLiteDatabase() {
        return mSqLiteDatabase;
    }

    public void closeDatabase() {
        if (mSqLiteDatabase != null && mSqLiteDatabase.isOpen()) {
            mSqLiteDatabase.close();
        }
    }

    public void init(Context context) {
        // 把数据库放到内存卡里面  判断是否有存储卡 6.0要动态申请权限
        File dbRoot = context.getDatabasePath("uuDownload.db");
        // 打开或者创建一个数据库
        mSqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbRoot, null);
    }
}
