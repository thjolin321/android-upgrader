package com.thjolin.compose;

import com.thjolin.download.util.Logl;

public class BsPatchUtils {

    static {
        try {
            System.loadLibrary("bspatch_utlis");
            Logl.e("loadLibrary成功");
        } catch (Exception e) {
            Logl.e("Exception: " + e.getMessage());
        }

    }

    public static native int patch(String oldApk, String newApk, String patchFile);


}
