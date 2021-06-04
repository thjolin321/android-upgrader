package com.example.updatefrompatch;

import android.os.Bundle;

import com.example.manager.DownloadManager;
import com.example.manager.listener.DownloadListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String url = "https://tdashi.xinchao.com/protal/files/tidashi.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DownloadManager.with().start(url, new DownloadListener() {
            @Override
            public void success(String path) {

            }

            @Override
            public void progress(int progress) {
                Log.e("TAG", progress + "");
            }

            @Override
            public void failed(String meg) {

            }
        });

    }
}