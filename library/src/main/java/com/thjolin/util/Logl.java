package com.thjolin.util;

import android.util.Log;

/**
 * Created by tanghao on 2021/6/2
 */
public class Logl {

    public static void e(String str) {
        if (str == null) return;
        Log.e("TAG", str);
    }

}