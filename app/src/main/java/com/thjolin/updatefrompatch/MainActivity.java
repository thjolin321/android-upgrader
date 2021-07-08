package com.thjolin.updatefrompatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaController;
import android.os.Build;
import android.os.Bundle;

import com.thjolin.install.InstallHelper;
import com.thjolin.update.Upgrader;
import com.thjolin.update.bean.ApkUpdateBean;
import com.thjolin.download.DownloadManager;
import com.thjolin.download.database.DownloadProvider;
import com.thjolin.download.listener.DownloadListener;
import com.thjolin.download.permission.MyPermissionActivity;
import com.thjolin.download.permission.core.IPermission;
import com.thjolin.download.permission.util.PermissionUtils;
import com.thjolin.download.task.DownloadTask;
import com.thjolin.util.Logl;
import com.thjolin.ui.ProgressDialog;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private static final String url = "https://s9.pstatp.com/package/apk/aweme/1015_160601/aweme_douyin_web1_v1015_160601_6d8a_1624978529.apk?v=1624978540";
//        private static final String url = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.article.pchome.net%2F00%2F37%2F68%2F81%2Fpic_lib%2Fs960x639%2F12325295252018x49zdm09qs960x639.jpg&refer=http%3A%2F%2Fimg.article.pchome.net&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1627633198&t=29a8b1abafead8cf5368c6695a331d77";
    String[] permissions = new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};

    ImageView aaa;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        aaa = findViewById(R.id.aaa);

    }

    DownloadTask task;

    public void onStart(View view) {
//        toInstallPermissionSettingIntent();
        testUpdate();
//        testInstall();
//        testDownload();
//        testPermission();
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void toInstallPermissionSettingIntent() {
//        Uri packageURI = Uri.parse("package:" + getPackageName());
//        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
//        startActivityForResult(intent, INSTALL_PERMISS_CODE);
//    }

//    private void initCacheDir() {
//        "/storage/emulated/0/Android/data/com.xinchao.elevator/cache/tp_read111.apk";
//        if (getApplicationContext().getExternalCacheDir() != null && isExistSDCard()) {
//            sCacheDir = getApplicationContext().getExternalCacheDir().toString();
//        } else {
//            sCacheDir = getApplicationContext().getCacheDir().toString();
//        }
//    }


    private void testInstall(){
//        "/storage/emulated/0/Android/data/com.xinchao.elevator/cache/tp_read111.apk";
        InstallHelper.installApkFinal(MainActivity.this,
                "/data/user/0/com.example.updatefrompatch/files/aweme_douyin_web1_v1015_160601_6d8a_1624978529.apk");
    }

    private void testPermission() {
        if (PermissionUtils.hasPermissionRequest(DownloadProvider.context, permissions)) {
            Logl.e("已有权限");
            return;
        }
        MyPermissionActivity.requestPermissionAction(DownloadProvider.context, permissions
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
        Logl.e("testUpdate");
        Upgrader.with().start(new ApkUpdateBean.Builder()
                .newApkUrl(url)
                .newApkVersionCode(2)
                .build());

    }

    private void testDownload() {
        ProgressDialog progressDialog = new ProgressDialog();
        DownloadTask.Builder configer = new DownloadTask.Builder();
        configer.url(url)
                .blockSize(10)
                .fileName("aaaa.apk")
                .forceRepeat(false)
                .fileParent(Environment.getExternalStorageDirectory().getPath() + File.separator + "tang")
                .needProgress(true);
        task = configer.build();
        DownloadManager.with().start(task, new DownloadListener() {
            @Override
            public void success(String path) {
                Logl.e("success: " + path);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        aaa.setImageBitmap(bitmap);
                    }
                });
            }

            @Override
            public void progress(int progress) {
                Log.e("进度TAG", progress + "");
                progressDialog.progress(progress);
            }

            @Override
            public void failed(String meg) {
                Logl.e("failed: " + meg);
            }
        });
        progressDialog.show(getSupportFragmentManager(), "ProgressDialog");
        progressDialog.setOnDialogClick(new ProgressDialog.OnDialogClick() {
            @Override
            public void onCancel() {
                task.cancel();
            }

            @Override
            public void onSure() {

            }
        });
    }

    public void onStop(View view) {
        task.cancel();
    }
}