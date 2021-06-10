package com.example.manager.constant;

/**
 * Created by tanghao on 2021/5/27
 */
public class Status {

    public static final int ERRO = -1;
    public static final int PAUSE = 100;
    public static final int DOWN = 200;
    public static String CHECK_URL = "请检查你的url";
    public static String DOWUNLOAD_ERROR = "下载出错";
    public static String CANEL = "用户取消下载";
    public static String MD5_UNMATCH = "md5值不匹配";

    public static boolean DEBUG = true;

    private int code; // -1 for wrong type， 200 for success;

    private String msg; // current msg;

    public int getCode() {
        return code;
    }

    public void setStatus(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}