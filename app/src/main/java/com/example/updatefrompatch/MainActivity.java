package com.example.updatefrompatch;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.library.Upgrader;
import com.example.library.bean.ApkPatchBean;
import com.example.library.bean.ApkUpdateBean;
import com.example.manager.DownloadManager;
import com.example.manager.database.DownloadProvider;
import com.example.manager.listener.DownloadListener;
import com.example.manager.permission.MyPermissionActivity;
import com.example.manager.permission.core.IPermission;
import com.example.manager.permission.util.PermissionUtils;
import com.example.manager.task.DownloadTask;
import com.example.manager.util.Logl;
import com.example.updatefrompatch.ui.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.security.Permission;
import java.security.Provider;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private static final String url = "https://tdashi.xinchao.com/protal/files/tidashi.apk";
    String[] permissions = new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//
//        String[] a = new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
//        requestPermissions(a, 0x856);

    }

    DownloadTask task;

    public void onStart(View view) {
//        testPermission();
        testUpdate();
//        testDownload();
    }

    private void testPermission() {
        if(PermissionUtils.hasPermissionRequest(DownloadProvider.context, permissions)){
            Logl.e("已有权限");
           return;
        }
        MyPermissionActivity.requestPermissionAction(DownloadProvider.context,permissions
                , 0x555, new IPermission() {
                    @Override
                    public void ganted() {
                        Logl.e("ganted");
                    }

                    @Override
                    public void cancel() {
                        Logl.e("cancel");
                    }

                    @Override
                    public void denied() {
                        Logl.e("denied");
                    }
                });
    }

    private void testUpdate() {
        Upgrader.with().start(new ApkUpdateBean.Builder()
                .newApkUrl(url)
                .newApkVersionCode(2)
                .build());
    }

    private void testDownload() {
        DownloadTask.Builder configer = new DownloadTask.Builder();
        configer.url(url)
                .blockSize(10)
                .fileName("tdashiapk")
                .forceRepeat(true)
                .needProgress(true);
        task = configer.build();
        DownloadManager.with().start(task, new DownloadListener() {
            @Override
            public void success(String path) {
                Logl.e("success: " + path);
            }

            @Override
            public void progress(int progress) {
                Log.e("进度TAG", progress + "");
            }

            @Override
            public void failed(String meg) {

            }
        });
    }

    public void onStop(View view) {
        task.cancel();
    }
}