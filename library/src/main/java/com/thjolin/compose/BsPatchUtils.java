package com.thjolin.compose;

public class BsPatchUtils {

    static{
//        System.loadLibrary("ApkPatch");
    }

    public static native int patch(String oldApk, String newApk, String patchFile);


}
