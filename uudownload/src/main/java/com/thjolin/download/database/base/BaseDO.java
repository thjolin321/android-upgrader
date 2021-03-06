package com.thjolin.download.database.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseDO implements Serializable {

    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private Map<String, Object> queryParams;

    public void addQueryParam(String key, Object value) {
        if (queryParams == null) {
            queryParams = new HashMap<>();
        }
        queryParams.put(key, value);
    }

    public void setQueryParams(Map<String, Object> queryParams) {
        this.queryParams = queryParams;
    }
}
