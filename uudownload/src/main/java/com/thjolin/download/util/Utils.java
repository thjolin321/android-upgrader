package com.thjolin.download.util;

import android.content.Context;
import android.util.DisplayMetrics;

import com.thjolin.download.database.DownloadProvider;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Created by th on 2021/6/3
 */
public class Utils {

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
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
        return names[names.length - 1].split("\\?")[0];
    }

    public static int[] getScreenWidthAndHeight(Context context){
        DisplayMetrics metrics2 = context.getResources().getDisplayMetrics();
        int width = metrics2.widthPixels;
        int height = metrics2.heightPixels;
        int wah[] = new int[2];
        wah[0] = width;
        wah[1] = height;
        return wah;
    }

    public static int getScreenWidth(){
        return DownloadProvider.context.getResources().getDisplayMetrics().widthPixels;
    }


}