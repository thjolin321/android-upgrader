package com.thjolin.download.permission.annotation;



import com.thjolin.download.permission.MyPermissionActivity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限申请注解
 */
@Target(ElementType.METHOD)  // 方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时
public @interface Permission {

    String[] value(); // 具体申请的权限

    // 默认的
    int requestCode() default MyPermissionActivity.PARAM_PERMSSION_CODE_DEFAULT;

}
