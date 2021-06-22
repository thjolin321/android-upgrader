package com.example.manager.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Created by tanghao on 2021/6/3
 */
public class DownloadUtils {

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            Logl.e("关闭流居然能报错？: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getFileNameFromUrl(String url) {
        if (url == null) {
            return null;
        }
        String[] names = url.split(File.separator);
        if (names.length == 0) {
            return null;
        }
        return names[names.length - 1];
    }

}