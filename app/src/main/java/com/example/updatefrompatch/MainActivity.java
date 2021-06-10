package com.example.updatefrompatch;

import android.os.Build;
import android.os.Bundle;

import com.example.manager.DownloadManager;
import com.example.manager.listener.DownloadListener;
import com.example.manager.task.DownloadTask;
import com.example.manager.util.Logl;
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

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private static final String url = "https://tdashi.xinchao.com/protal/files/tidashi.apk";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] a = new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
        requestPermissions(a, 0x856);

    }

    DownloadTask task;

    public void onStart(View view) {
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