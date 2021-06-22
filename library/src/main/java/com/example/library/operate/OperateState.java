package com.example.library.operate;

/**
 * Created by tanghao on 2021/6/11
 */
public class OperateState {


    /**
     * 0 for check
     * 10 download hole apk
     * 11 download patch
     * 20 compose apk
     * 100 install apk
     * 200 install success
     *
     * -10 download failed
     * -20 compose failed
     * -100 install failed permission
     *
     * 1000 goto market
     */
    public int state;

}