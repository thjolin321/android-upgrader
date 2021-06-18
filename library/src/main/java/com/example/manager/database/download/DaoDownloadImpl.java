package com.example.manager.database.download;

import android.database.Cursor;

import com.example.manager.database.DownloadEntity;
import com.example.manager.database.base.BaseDbImpl;
import com.example.manager.util.Logl;

import java.util.List;

/**
 * Created by tanghao on 2021/6/8
 */
public class DaoDownloadImpl extends BaseDbImpl<DownloadEntity> implements DaoDownload {

    @Override
    public long qureyAllCacheSize(String url) {
        long size = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("select sum (progress) from ")
                .append(getTableName())
                .append(" where url = '")
                .append(url)
                .append("'");
        Logl.e("查询url:" + sb.toString());
        Cursor cursor = getmSqLiteDatabase().rawQuery(sb.toString(), null);
        if (cursor.moveToFirst()) {
            size = cursor.getLong(0);
        }
        cursor.close();
        DownloadDaoFatory.getDao();
        return size;
    }

    @Override
    public List<DownloadEntity> qureyAllByUrl(String url) {
        Logl.e("getmQuerySupport()==null :" + (getmQuerySupport() == null));
        return getmQuerySupport().selection("url = '" + url + "'").orderBy("threadId").query();
    }

    @Override
    public long deleteByUrl(String url) {
        long a = getmSqLiteDatabase().delete(getTableName(), "url = '" + url + "'", null);
        Logl.e("删除结果：" + a);
        return a;
    }

}