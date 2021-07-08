package com.thjolin.download.permission.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.thjolin.download.database.DownloadProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PermissionUtils {

    /**
     * TODO 检查是否需要去请求权限，此方法目的：就是检查 是否已经授权了
     *
     * @param context
     * @param permissions
     * @return 返回false代表需要请求权限，  返回true代表不需要请求权限 就可以结束MyPermisisonActivity了
     */
    public static boolean hasPermissionRequest(Context context, String... permissions) {
        for (String permission : permissions) {
            if (isPermissionRequest(context, permission) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasFilePermission() {
        if(!isPermissionRequest(DownloadProvider.context, WRITE_EXTERNAL_STORAGE)){
            return false;
        }
        if(!isPermissionRequest(DownloadProvider.context, READ_EXTERNAL_STORAGE)){
            return false;
        }
        return true;
    }

    /**
     * TODO 判断参数中传递进去的权限是否已经被授权了
     *
     * @param context
     * @param permission
     * @return
     */
    private static boolean isPermissionRequest(Context context, String permission) {
        try {
            int checkSelfPermission = ContextCompat.checkSelfPermission(context, permission);
            return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

    // TODO 最后判断下 是否真正的成功
    public static boolean requestPermissionSuccess(int... gantedResult) {
        if (gantedResult == null || gantedResult.length <= 0) {
            return false;
        }

        for (int permissionValue : gantedResult) {
            if (permissionValue != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // TODO 说白了：就是用户被拒绝过一次，然后又弹出这个框，【需要给用户一个解释，为什么要授权，就需要执行此方法判断】
    // 当用户点击了不再提示，这种情况要考虑到才行
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }


    // TODO 专门去 回调 MainActivity (被@PermissionCancel/PermissionDenied) 的 函数
    public static void invokeAnnotion(Object object, Class annotionClass) {
        Class<?> objectClass = object.getClass(); // 可能是 MainActivity

        // 遍历所有函数
        Method[] methods = objectClass.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true); // 让虚拟机不要去检测 private

            // 判断是否被 annotionClass 注解过的函数
            boolean annotationPresent = method.isAnnotationPresent(annotionClass);

            if (annotationPresent) {
                // 当前方法 annotionClass 注解过的函数
                try {
                    method.invoke(object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
