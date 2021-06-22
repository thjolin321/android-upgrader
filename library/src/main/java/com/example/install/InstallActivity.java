package com.example.install;

import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.library.R;

/**
 * Created by tanghao on 2021/6/22
 */
public class InstallActivity extends AppCompatActivity {

    private static final String ACTION_INSTALL = "com.uu.install";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_permission);
    }

}