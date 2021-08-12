package com.thjolin.download.database.download;

import android.database.Cursor;

import com.thjolin.download.database.DownloadEntity;
import com.thjolin.download.database.base.BaseDbImpl;
import com.thjolin.download.util.Logl;

import java.util.ArrayList;
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
        // 手写sql，不用反射，提升效率
        String sql = "SELECT * FROM DownloadEntity WHERE url = '" + url + "' ORDER BY threadId";
        ArrayList<DownloadEntity> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            Logl.e("sql: " + sql);
            cursor = getmSqLiteDatabase().rawQuery(sql, null);
            if (cursor != null) {
                if (!cursor.moveToFirst()) {
                    return null;
                }
                do {
                    long id = cursor.getLong(cursor.getColumnIndex("id"));
                    int threadId = cursor.getInt(cursor.getColumnIndex("threadId"));
                    long progress = cursor.getLong(cursor.getColumnIndex("progress"));
                    long contentLength = cursor.getInt(cursor.getColumnIndex("contentLength"));
                    long start = cursor.getInt(cursor.getColumnIndex("start"));
                    String urlName = cursor.getString(cursor.getColumnIndex("url"));
                    list.add(new DownloadEntity(id, start, urlName, threadId, progress, contentLength));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Logl.e("sql错误：" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    @Override
    public long deleteByUrl(String url) {
        long a = getmSqLiteDatabase().delete(getTableName(), "url = '" + url + "'", null);
        Logl.e("删除结果：" + a);
        return a;
    }

}