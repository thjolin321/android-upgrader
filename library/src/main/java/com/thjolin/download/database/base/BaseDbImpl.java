package com.thjolin.download.database.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.collection.ArrayMap;

import com.thjolin.download.database.DaoUtil;
import com.thjolin.util.Logl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BaseDbImpl<T extends BaseDO> implements BaseDb<T> {

    private SQLiteDatabase mSqLiteDatabase;

    // 泛型类
    private Class<T> mClazz;

    private static final Object[] mPutMethodArgs = new Object[2];

    private static final Map<String, Method> mPutMethods
            = new ArrayMap<>();

    QuerySupport mQuerySupport;

    @Override
    public void init(SQLiteDatabase sqLiteDatabase, Class<T> clazz) {
        this.mSqLiteDatabase = sqLiteDatabase;
        this.mClazz = clazz;
        String tableName = DaoUtil.getTableName(mClazz);
        mQuerySupport = new QuerySupport(sqLiteDatabase, clazz);
        if (isTableExists(tableName)) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("create table if not exists ")
                .append(tableName)
                .append("(id integer primary key autoincrement, ");
        Field[] fields = mClazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);// 设置权限
            String name = field.getName();
            String type = field.getType().getSimpleName();// int String boolean
            //  type需要进行转换 int --> integer, String text;
            sb.append(name).append(DaoUtil.getColumnType(type)).append(", ");
        }
        sb.replace(sb.length() - 2, sb.length(), ")");
        String createTableSql = sb.toString();
        Log.e("TAG", "表语句--> " + createTableSql);
        // 创建表
        mSqLiteDatabase.execSQL(createTableSql);
    }

    public boolean isTableExists(String tableName) {
        Cursor cursor = mSqLiteDatabase
                .rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'",
                        null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    @Override
    public String getTableName() {
        return DaoUtil.getTableName(mClazz);
    }

    @Override
    public synchronized long insert(T obj) {
        Logl.e("开始insert: " + mClazz + "====");
        return mSqLiteDatabase.insert(DaoUtil.getTableName(mClazz), null, contentValuesByObj(obj));
    }

    @Override
    public synchronized void insert(List<T> list) {
        // 批量插入采用 事物
        mSqLiteDatabase.beginTransaction();
        for (T data : list) {
            // 调用单条插入
            data.setId(insert(data));
        }
        mSqLiteDatabase.setTransactionSuccessful();
        mSqLiteDatabase.endTransaction();
    }

    @Override
    public synchronized long insertOrUpdate(T obj) {
        if (obj.getId() == 0) {
            return insert(obj);
        }
        return updateByPrimaryKey(obj, obj.getId());
    }

    @Override
    public synchronized long deletetByPrimaryKey(long id) {
        return mSqLiteDatabase.delete(DaoUtil.getTableName(mClazz), "id = " + id, null);
    }

    @Override
    public synchronized long deletetByParamMap(Map<String, Object> paramMap) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            sb.append(entry.getKey())
                    .append(" = ")
                    .append(entry.getValue())
                    .append(" and ");
        }
        if (sb.length() == 0) {
            return -1;
        }
        return mSqLiteDatabase.delete(DaoUtil.getTableName(mClazz),
                sb.subSequence(0, sb.length() - 4).toString(), null);
    }

    @Override
    public synchronized long updateByPrimaryKey(T obj, long id) {
        return mSqLiteDatabase.update(DaoUtil.getTableName(mClazz),
                contentValuesByObj(obj), "id = '" + id + "'", null);
    }

    @Override
    public T selectByPrimaryKey(int id) {
        return mQuerySupport.selection("id = " + id).queryLimiteOne();
    }

    @Override
    public List<T> selectByMap(Map<String, Object> paramMap) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            sb.append(entry.getKey())
                    .append(" = ")
                    .append(entry.getValue())
                    .append(" and ");
        }
        return mQuerySupport.selection(sb.length() == 0 ? "" : sb.substring(0, 4)).query();
    }

    // obj 转成 ContentValues
    private ContentValues contentValuesByObj(T obj) {
        // 第三方的 使用比对一下 了解一下源码
        ContentValues values = new ContentValues();
        // 封装values
        Field[] fields = mClazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                // 设置权限，私有和共有都可以访问
                field.setAccessible(true);
                String key = field.getName();
                // 获取value
                Object value = field.get(obj);
                // put 第二个参数是类型  把它转换

                mPutMethodArgs[0] = key;
                mPutMethodArgs[1] = value;
                // 方法使用反射 ， 反射在一定程度上会影响性能
                String filedTypeName = field.getType().getName();
                // 还是使用反射  获取方法  put  缓存方法
                Method putMethod = mPutMethods.get(filedTypeName);
                if (putMethod == null) {
                    putMethod = ContentValues.class.getDeclaredMethod("put", String.class, value.getClass());
                    mPutMethods.put(filedTypeName, putMethod);
                }

                // 通过反射执行
                putMethod.invoke(values, mPutMethodArgs);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mPutMethodArgs[0] = null;
                mPutMethodArgs[1] = null;
            }
        }
        return values;
    }

    public SQLiteDatabase getmSqLiteDatabase() {
        return mSqLiteDatabase;
    }

    public QuerySupport getmQuerySupport() {
        return mQuerySupport;
    }

    public class QuerySupport {
        // 查询的列
        private String[] mQueryColumns;
        // 查询的条件
        private String mQuerySelection;
        // 查询的参数
        private String[] mQuerySelectionArgs;
        // 查询分组
        private String mQueryGroupBy;
        // 查询对结果集进行过滤
        private String mQueryHaving;
        // 查询排序
        private String mQueryOrderBy;
        // 查询可用于分页
        private String mQueryLimit;

        private SQLiteDatabase mSQLiteDatabase;

        public QuerySupport(SQLiteDatabase sqLiteDatabase, Class<T> clazz) {
            this.mSQLiteDatabase = sqLiteDatabase;
        }

        public QuerySupport columns(String... columns) {
            this.mQueryColumns = columns;
            return this;
        }

        public QuerySupport selectionArgs(String... selectionArgs) {
            this.mQuerySelectionArgs = selectionArgs;
            return this;
        }

        public QuerySupport having(String having) {
            this.mQueryHaving = having;
            return this;
        }

        public QuerySupport orderBy(String orderBy) {
            this.mQueryOrderBy = orderBy;
            return this;
        }

        public QuerySupport limit(String limit) {
            this.mQueryLimit = limit;
            return this;
        }

        public QuerySupport groupBy(String groupBy) {
            this.mQueryGroupBy = groupBy;
            return this;
        }

        public QuerySupport selection(String selection) {
            this.mQuerySelection = selection;
            return this;
        }

        public List<T> query() {
            Cursor cursor = null;
            try {
                cursor = mSQLiteDatabase.query(DaoUtil.getTableName(mClazz), mQueryColumns, mQuerySelection,
                        mQuerySelectionArgs, mQueryGroupBy, mQueryHaving, mQueryOrderBy, mQueryLimit);
                Logl.e("query: " + mQuerySelectionArgs[0]);
            } catch (Exception e) {
                Logl.e("query e :" + e.getMessage());
            }
            clearQueryParams();
            return cursorToList(cursor);
        }

        public T queryLimiteOne() {
            Cursor cursor = mSQLiteDatabase.query(DaoUtil.getTableName(mClazz), mQueryColumns, mQuerySelection,
                    mQuerySelectionArgs, mQueryGroupBy, mQueryHaving, mQueryOrderBy, mQueryLimit);
            Logl.e("query: " + mQuerySelectionArgs[0]);
            clearQueryParams();
            return cursorToList(cursor).get(0);
        }

        public List<T> queryAll() {
            Cursor cursor = mSQLiteDatabase.query(DaoUtil.getTableName(mClazz), null, null, null, null, null, null);
            return cursorToList(cursor);
        }

        /**
         * 清空参数
         */
        private void clearQueryParams() {
            mQueryColumns = null;
            mQuerySelection = null;
            mQuerySelectionArgs = null;
            mQueryGroupBy = null;
            mQueryHaving = null;
            mQueryOrderBy = null;
            mQueryLimit = null;
        }

        /**
         * 通过Cursor封装成查找对象
         *
         * @return 对象集合列表
         */
        private List<T> cursorToList(Cursor cursor) {
            List<T> list = new ArrayList<>();
            Logl.e("cursorToList: " + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    try {
                        T instance = mClazz.newInstance();
                        Field[] fields = mClazz.getDeclaredFields();
                        for (Field field : fields) {
                            // 遍历属性
                            field.setAccessible(true);
                            String name = field.getName();
                            // 获取角标
                            int index = cursor.getColumnIndex(name);
                            if (index == -1) {
                                continue;
                            }
                            // 通过反射获取 游标的方法
                            Method cursorMethod = cursorMethod(field.getType());
                            if (cursorMethod != null) {
                                Object value = cursorMethod.invoke(cursor, index);
                                if (value == null) {
                                    continue;
                                }
                                // 处理一些特殊的部分
                                if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                                    if ("0".equals(String.valueOf(value))) {
                                        value = false;
                                    } else if ("1".equals(String.valueOf(value))) {
                                        value = true;
                                    }
                                } else if (field.getType() == char.class || field.getType() == Character.class) {
                                    value = ((String) value).charAt(0);
                                } else if (field.getType() == Date.class) {
                                    long date = (Long) value;
                                    if (date <= 0) {
                                        value = null;
                                    } else {
                                        value = new Date(date);
                                    }
                                }
                                field.set(instance, value);
                            }
                        }
                        Logl.e("cursorToList: " + instance);
                        // 加入集合
                        list.add(instance);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Logl.e("cursorToList: " + e);

                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            return list;
        }

        private Method cursorMethod(Class<?> type) throws Exception {
            String methodName = getColumnMethodName(type);
            Method method = Cursor.class.getMethod(methodName, int.class);
            return method;
        }

        private String getColumnMethodName(Class<?> fieldType) {
            String typeName;
            if (fieldType.isPrimitive()) {
                typeName = DaoUtil.capitalize(fieldType.getName());
            } else {
                typeName = fieldType.getSimpleName();
            }
            String methodName = "get" + typeName;
            if ("getBoolean".equals(methodName)) {
                methodName = "getInt";
            } else if ("getChar".equals(methodName) || "getCharacter".equals(methodName)) {
                methodName = "getString";
            } else if ("getDate".equals(methodName)) {
                methodName = "getLong";
            } else if ("getInteger".equals(methodName)) {
                methodName = "getInt";
            }
            return methodName;
        }
    }


}
