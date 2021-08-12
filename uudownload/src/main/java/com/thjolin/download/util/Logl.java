package com.thjolin.download.util;

import android.util.Log;

/**
 * Created by tanghao on 2021/6/2
 */
public class Logl {

    private static boolean DEBUG = true;

    public static void e(String str) {
        if (str == null && !DEBUG) return;
        Log.e("TAG", str);
    }

    public static void setDEBUG(boolean debug) {
        DEBUG = debug;
    }

}