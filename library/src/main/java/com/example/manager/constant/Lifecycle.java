package com.example.manager.constant;

/**
 * Created by tanghao on 2021/5/27
 */
public interface Lifecycle {

    void init();

    void prepare();

    void start();

    void destroy();

}
