package com.thjolin.download.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.uudownload.R;
import com.thjolin.download.permission.core.IPermission;
import com.thjolin.download.permission.util.PermissionUtils;
import com.thjolin.download.util.Logl;


public class MyPermissionActivity extends Activity {

    // 定义权限处理的标记， ---- 接收用户传递进来的
    private final static String PARAM_PERMSSION = "param_permission";
    private final static String PARAM_PERMSSION_CODE = "param_permission_code";
    public final static int PARAM_PERMSSION_CODE_DEFAULT = -1;

    // 真正接收存储的变量
    private String[] permissions;
    private int requestCode;
    // 方便回调的监听  告诉外交 已授权，被拒绝，被取消
    private static IPermission iPermissionListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tang_permission); // 透明

        if (getIntent().getBooleanExtra("storageManager", false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    //跳转新页面申请权限
                    startActivityForResult(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION), 101);
                }
            }
            return;
        }

        // 我们就接收到了
        permissions = getIntent().getStringArrayExtra(PARAM_PERMSSION); // READ_EXTERNAL_STORAGE
        requestCode = getIntent().getIntExtra(PARAM_PERMSSION_CODE, PARAM_PERMSSION_CODE_DEFAULT);

        if (permissions == null && requestCode < 0 && iPermissionListener == null) {
            this.finish();
            return;
        }

        // 能够走到这里，就开始去检查，是否已经授权了
        boolean permissionRequest = PermissionUtils.hasPermissionRequest(this, permissions);
        if (permissionRequest) {
            // 通过监听接口，告诉外交，已经授权了
            iPermissionListener.ganted();

            this.finish();
            return;
        }

        // 能够走到这里，就证明，还需要去申请权限
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    // 申请的结果
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) { // 如果申请三个权限  grantResults.len = 3
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 返回的结果，需要去验证一下，是否完全成功
        if (PermissionUtils.requestPermissionSuccess(grantResults)) {
            iPermissionListener.ganted(); // 已经授权成功了

            this.finish();
            return;
        }

        // 没有成功，可能是用户 不听话
        // 如果用户点击了，拒绝（勾选了”不再提醒“） 等操作
        if (!PermissionUtils.shouldShowRequestPermissionRationale(this, permissions)) {
            // 用户拒绝，不再提醒
            iPermissionListener.denied();

            this.finish();
            return;
        }

        // 取消
        iPermissionListener.cancel();
        this.finish();
        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //申请权限结果
        if (requestCode == 101) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    iPermissionListener.ganted();
                } else {
                    iPermissionListener.denied();
                }
            }
        }
    }

    // 让此Activity不要有任何动画
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public static void requestPermissionAction(Context context, String[] permissions,
                                               int requestCode, IPermission iPermissionListener) {
        Logl.e("PermissionActivity: requestPermissionAction");
        MyPermissionActivity.iPermissionListener = iPermissionListener;
        Intent intent = new Intent(context, MyPermissionActivity.class);
        // 效果
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_PERMSSION_CODE, requestCode);
        bundle.putStringArray(PARAM_PERMSSION, permissions);

        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    // TODO 此权限申请专用的Activity ，对外暴露， static
    public static void requestStorageManagerPermission(Context context, IPermission iPermissionListener) {
        MyPermissionActivity.iPermissionListener = iPermissionListener;
        Intent intent = new Intent(context, MyPermissionActivity.class);
        // 效果
        intent.putExtra("storageManager", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

}
