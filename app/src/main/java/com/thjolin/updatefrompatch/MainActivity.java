package com.thjolin.updatefrompatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import com.thjolin.download.dispatcher.TaskDispatcher;
import com.thjolin.download.listener.DownloadListenerWithSpeed;
import com.thjolin.download.listener.MultiDownloadListener;
import com.thjolin.install.InstallHelper;
import com.thjolin.ui.DefaultActivityController;
import com.thjolin.ui.PDialog;
import com.thjolin.update.Upgrader;
import com.thjolin.update.bean.ApkPatchBean;
import com.thjolin.update.bean.ApkUpdateBean;
import com.thjolin.download.UuDownloader;
import com.thjolin.download.database.DownloadProvider;
import com.thjolin.download.listener.DownloadListener;
import com.thjolin.download.permission.MyPermissionActivity;
import com.thjolin.download.permission.core.IPermission;
import com.thjolin.download.permission.util.PermissionUtils;
import com.thjolin.download.task.DownloadTask;
import com.thjolin.download.util.Logl;
import com.thjolin.update.configer.UpgraderConfiger;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private static final String url = "https://tdashi-01.obs.cn-east-3.myhuaweicloud.com/Box/Test/update_debug.apk";
//    private static final String url = "https://gdown.baidu.com/data/wisegame/a99740bf3b68ba9e/3a07a99740bf3b68ba9e4cf01b5d1928.apk";
//    private static final String url = "https://tdashi-01.obs.cn-east-3.myhuaweicloud.com/Box/Log/G7FXO0RFL4/app-debug.apk";
    private static final String url1 = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.article.pchome.net%2F00%2F37%2F68%2F81%2Fpic_lib%2Fs960x639%2F12325295252018x49zdm09qs960x639.jpg&refer=http%3A%2F%2Fimg.article.pchome.net&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1627633198&t=29a8b1abafead8cf5368c6695a331d77";
    private static final String url2 = "https://s9.pstatp.com/package/apk/lark/1583_40455/lark_feishu_website_organic_and_v1583_40455_7ea0_1626081724.apk?v=1626081732";
    private static final String url3 = "https://dldir1.qq.com/weixin/android/weixin807android1920_arm64.apk";
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
//        Stetho.initializeWithDefaults(this);
//        startActivity(new Intent(this, LoginActivity.class));
    }

    DownloadTask task;

    public void onStart(View view) {
        Logl.e("onStart");
        // 普通更新
        testUpdate();
        // 传入文件安装
//        testInstall();
        // 下载测试，带UI
//        testDownload();
        // 带速度的下载测试
//        speedTest();
        // 权限申请框架
//        testPermission();
        // 多任务下载
//        testMultiDownload();
    }

    private void testMultiDownload() {
        List<String> list = new ArrayList<>();
        list.add(url);
        list.add(url1);
        list.add(url2);
        list.add(url3);
        UuDownloader.with().start(list, new MultiDownloadListener() {
            @Override
            public void onFinish() {
                Logl.e("onFinish:");
            }

            @Override
            public void onSuccess(String url, String path) {
                Logl.e("onSuccess: " + url + "  path  " + path);
            }

            @Override
            public void onFailed(String url) {
                Logl.e("onFailed: " + url);
            }
        });
    }

    private void testInstall() {
//        "/storage/emulated/0/Android/data/com.xinchao.elevator/cache/tp_read111.apk";
        InstallHelper.installWithActivity(
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
        // 在此EditText中输入 patch文件的下载的地址，本该服务器返回。
        EditText fab111 = findViewById(R.id.fab111);
        Upgrader.with().setDebug(false).setConfiger(new UpgraderConfiger
                .Builder()
                .forceUpdate(true)
                .silent(false)
                .needNotifycation(true)
                .showDownladProgress(true)
                .closeUi(false)
                .uiListener(DefaultActivityController.getInstance())
                .build()).start(new ApkUpdateBean.Builder()
                .newApkUrl(url)
                .newApkVersionCode(3)
                // 测试 增量更新时使用
//                .addApkPatchBean(new ApkPatchBean(1, fab111.getText().toString()))
                .build());
    }

    private void speedTest() {
        PDialog progressDialog = new PDialog(this);
        DownloadTask.Builder configer = new DownloadTask.Builder();
        configer.url(url3)
                .blockSize(10)
                .newFileMd5("df2f045dfa854d8461d9cefe08b813c8")
                .fileName("a1111111.apk")
                .forceRepeat(false)
                .needSpeed(true)
                .needProgress(true);
        task = configer.build();
        UuDownloader.with().start(task, new DownloadListenerWithSpeed() {

            @Override
            public void speed(String speed) {
                progressDialog.showSpeed(speed);
            }

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
                progressDialog.progress(progress);
            }

            @Override
            public void failed(String meg) {
                Logl.e("failed: " + meg);
            }
        });
        progressDialog.show();
        progressDialog.setOnDialogClick(new PDialog.OnDialogClick() {
            @Override
            public void onCancel() {
                Logl.e("tack.cancel Click");
                task.cancel();
            }

            @Override
            public void onSure() {
                Logl.e("onSure Click");
            }
        });
    }

    private void testDownload() {
        PDialog progressDialog = new PDialog(this);
        DownloadTask.Builder configer = new DownloadTask.Builder();
        configer.url(url)
                .blockSize(10)
                .forceRepeat(false)
                .needProgress(true);
        task = configer.build();
        UuDownloader.with().start(task, new DownloadListener() {
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
                Logl.e("进度TAG000000000" + progress);
                progressDialog.progress(progress);
            }

            @Override
            public void failed(String meg) {
                Logl.e("failed: " + meg);
            }
        });
        progressDialog.show();
        progressDialog.setOnDialogClick(new PDialog.OnDialogClick() {
            @Override
            public void onCancel() {
                task.cancel();
            }

            @Override
            public void onSure() {
                TaskDispatcher.getInstance().inspectRunningAndWait();
            }
        });


        UuDownloader.with().start(new DownloadTask.Builder().url(url1).build(), new DownloadListener() {
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
                Logl.e("进度TAG1111111" + progress);
                progressDialog.progress(progress);
            }

            @Override
            public void failed(String meg) {
                Logl.e("failed: " + meg);
            }
        });
        UuDownloader.with().start(new DownloadTask.Builder().url("https://dl.softmgr.qq.com/original/Audio/QQMusic_Setup_1733.4793_QMgr.exe")
                .build(), new DownloadListener() {
            @Override
            public void success(String path) {
                Logl.e("success222: " + path);
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
                Logl.e("进度TAG22222222" + progress);
                progressDialog.progress(progress);
            }

            @Override
            public void failed(String meg) {
                Logl.e("failed: " + meg);
            }
        });

        UuDownloader.with().start(new DownloadTask.Builder().url(url2)
                .build(), new DownloadListener() {
            @Override
            public void success(String path) {
                Logl.e("success33: " + path);
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
                Logl.e("进度TAG33333" + progress);
                progressDialog.progress(progress);
            }

            @Override
            public void failed(String meg) {
                Logl.e("failed: " + meg);
            }
        });

        UuDownloader.with().start(new DownloadTask.Builder().url(url)
                .fileName("aaaaa11111")
                .build(), null);
        UuDownloader.with().start(new DownloadTask.Builder().url(url)
                .fileName("aaaaa22222")
                .build(), null);

        UuDownloader.with().start(new DownloadTask.Builder().url(url3)
                .fileName("aaaaa222221111111")
                .build(), null);
    }

    public void onStop(View view) {
        task.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Upgrader.with().destroy();
    }
}