package com.thjolin.download.permission.aspect;//package com.example.manager.permission.aspect;
//
//import android.content.Context;
//
//import androidx.fragment.app.Fragment;
//
//
//import com.example.manager.permission.MyPermissionActivity;
//import com.example.manager.permission.annotation.Permission;
//import com.example.manager.permission.annotation.PermissionCancel;
//import com.example.manager.permission.annotation.PermissionDenied;
//import com.example.manager.permission.core.IPermission;
//import com.example.manager.permission.util.PermissionUtils;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//
//@Aspect
//public class PermissionAspect {
//
//    // 切点 == Permission
//    // 切入点 == 我们要监听的注解
//    // 仅仅是只能找到这个函数
//    @Pointcut
//("execution(@com.derry.premissionstudy.permission.annotation.Permission * *(..)) && @annotation(permission)") //  @Permission == permission
//    public void pointActionMethod(Permission permission) {} // 切点函数
//
//    // TODO 切面
//    @Around("pointActionMethod(permission)")
//    public void aProceedingJoinPoint(final ProceedingJoinPoint point, Permission permission) throws Throwable {
//        // 先定义一个上下文操作创建
//        Context context = null;
//
//        final Object thisObject = point.getThis(); // thisObject == null 环境有问题
//
//        // context初始化
//        if (thisObject instanceof Context) {
//            context = (Context) thisObject;
//        } else if (thisObject instanceof Fragment) {
//            context = ((Fragment) thisObject).getActivity();
//        }
//
//        // 判断是否为null
//        if (null == context || permission == null) {
//            throw new IllegalAccessException("null == context || permission == null is null");
//        }
//
//        final Context finalContext = context;
//
//
//        // 调用 空白的 Activity 申请权限
//        MyPermissionActivity.requestPermissionAction(context, permission.value(), permission.requestCode(), new IPermission() {
//            // 已经授权
//            @Override
//            public void ganted() {
//                // 申请成功
//                try {
//                    point.proceed(); // 被Permission 的函数，正常执行下去，不拦截
//                } catch (Throwable throwable) {
//                    throwable.printStackTrace();
//                }
//            }
//
//            @Override
//            public void cancel() {
//                // 被拒绝
//                PermissionUtils.invokeAnnotion(thisObject, PermissionCancel.class);
//            }
//
//            @Override
//            public void denied() {
//                // 被拒绝（不再提醒的）
//                PermissionUtils.invokeAnnotion(thisObject, PermissionDenied.class);
//            }
//        });
//    }
//}
