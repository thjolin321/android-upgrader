package com.thjolin.install;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.thjolin.update.R;

/**
 * Created by tanghao on 2021/6/22
 */
public class InstallActivity extends AppCompatActivity {

    public static final String ACTION_INSTALL = "com.uu.install";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent_layout);

    }

}