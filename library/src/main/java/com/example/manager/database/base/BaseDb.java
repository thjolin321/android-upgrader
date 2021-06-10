package com.example.manager.database.base;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;
import java.util.Map;

public interface BaseDb<T> {

    void init(SQLiteDatabase sqLiteDatabase, Class<T> clazz);

    String getTableName();

    // C
    long insert(T obj);

    long insertOrUpdate(T obj);

    void insert(List<T> list);

    // R
    long deletetByPrimaryKey(long id);

    long deletetByParamMap(Map<String, Object> paramMap);

    // U
    long updateByPrimaryKey(T obj, long id);

    // D
    T selectByPrimaryKey(int id);

    List<T> selectByMap(Map<String, Object> paramMap);


}
