package com.thjolin.updatefrompatch.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.thjolin.compose.BsPatchUtils;
import com.thjolin.updatefrompatch.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import dalvik.system.PathClassLoader;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testDiff();
    }


    public void testDiff() {
        File file = copy4Assets();
        PathClassLoader classLoader = new PathClassLoader(file.getAbsolutePath(), getClassLoader());
        try {
            Class<?> test = classLoader.loadClass("Test");
            Method main = test.getDeclaredMethod("test");
            main.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File copy4Assets() {
        File externalFilesDir = getExternalFilesDir("");
        File dexFile = new File(externalFilesDir, "new.dex");
        if (!dexFile.exists()) {
            BufferedInputStream is = null;
            BufferedOutputStream os = null;
            try {
                is = new BufferedInputStream(getAssets().open("new2.dex"));
                os = new BufferedOutputStream(new FileOutputStream(dexFile));
                byte[] buffer = new byte[4096];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dexFile;

    }

    public void onStart(View view) {
        File newFile = new File(getExternalFilesDir("apk"), "app.apk");
        File patchFile = new File(getExternalFilesDir("apk"), "patch.apk");
        int result = BsPatchUtils.patch(getApplicationInfo().sourceDir, newFile.getAbsolutePath(),
                patchFile.getAbsolutePath());
        if (result == 0) {
            install(newFile);
        }
    }

    private void install(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本
            Uri apkUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }
}
