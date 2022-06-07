package com.thjolin.install;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.thjolin.ui.DefaultActivityController;
import com.thjolin.update.R;
import com.thjolin.download.util.Logl;
import com.thjolin.download.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InstallApkActivity extends FragmentActivity implements OnClickListener {

    private OnDialogClick onDialogClick;
    private ProgressBar bar;
    private TextView tvProgress;
    private View llProgress;
    private boolean needCompose;
    private boolean onceInstall = true;
    private boolean onceClick = true;
    private TextView tvContent;


    private static final String PACKAGE_INSTALLED_ACTION =
            "com.thjolin.apkinstall.SESSION_API_PACKAGE_INSTALLED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logl.e("InstallApkActivity.onCreate");
        if (!getIntent().getBooleanExtra("needUi", false)) {
            setContentView(R.layout.activity_transparent_layout);
            startInstall(getIntent().getStringExtra("apkPath"));
        } else {
            setContentView(R.layout.progress_tang_update);
            bar = findViewById(R.id.progress_bar);
            tvProgress = findViewById(R.id.tv_progress);
            llProgress = findViewById(R.id.ll_progress);
            // Watch for button clicks.
            setTitle(null);
            findViewById(R.id.bt_sure).setOnClickListener(this);
            findViewById(R.id.bt_close).setOnClickListener(this);
            findViewById(R.id.bt_cancel).setOnClickListener(this);
            tvContent = findViewById(R.id.dialog_content);
            DefaultActivityController.getInstance().setActivity(this);
            if (getIntent().getBooleanExtra("showNotification", false)) {
                DefaultActivityController.getInstance().showNotification();
            }
            if (getIntent().getBooleanExtra("forceUpdate", false)) {
                setForceUpdate();
            }
            if (getIntent().getBooleanExtra("needDownload", false)) {
                setNoNeedProgress();
            }
            if (getIntent().getBooleanExtra("needCompose", false)) {
                setNeedCompose();
            }
        }
    }

    private void startInstall(String path) {
        InstallHelper.installApkWithIntent(this, new File(path));
    }

    // Note: this Activity must run in singleTop launchMode for it to be able to receive the intent
    // in onNewIntent().
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logl.e("onNewIntent");
        Bundle extras = intent.getExtras();
        Logl.e("安装监听：" + intent.getAction());
        if (PACKAGE_INSTALLED_ACTION.equals(intent.getAction())) {
            int status = extras.getInt(PackageInstaller.EXTRA_STATUS);
            String message = extras.getString(PackageInstaller.EXTRA_STATUS_MESSAGE);
            Logl.e("message: " + message);
            Logl.e("status: " + status);
            switch (status) {
                case PackageInstaller.STATUS_PENDING_USER_ACTION:
                    // This test app isn't privileged, so the user has to confirm the install.
                    Intent confirmIntent = (Intent) extras.get(Intent.EXTRA_INTENT);
                    startActivity(confirmIntent);
                    break;
                case PackageInstaller.STATUS_SUCCESS:
                    Toast.makeText(this, "安装成功!", Toast.LENGTH_SHORT).show();
                    break;
                case PackageInstaller.STATUS_FAILURE:
                case PackageInstaller.STATUS_FAILURE_ABORTED:
                case PackageInstaller.STATUS_FAILURE_BLOCKED:
                case PackageInstaller.STATUS_FAILURE_CONFLICT:
                case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
                case PackageInstaller.STATUS_FAILURE_INVALID:
                case PackageInstaller.STATUS_FAILURE_STORAGE:
                    Toast.makeText(this, "安装失败! " + status + ", " + message,
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Unrecognized status received from installer: " + status,
                            Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!onceClick) {
            return;
        }
        onceClick = false;
        int id = v.getId();
        if (id == R.id.bt_sure) {
            if (onDialogClick != null) {
                onDialogClick.onSure();
                ((TextView) findViewById(R.id.bt_sure)).setText(R.string.uu_downloading);
                if (getIntent().getBooleanExtra("showNotification", false)) {
                    finish();
                }
            } else {
                startInstall(getIntent().getStringExtra("apkPath"));
            }
        } else {
            if (onDialogClick != null) {
                onDialogClick.onCancel();
            }
            finish();
        }
    }


    public void setOnDialogClick(OnDialogClick onDialogClick) {
        this.onDialogClick = onDialogClick;
    }

    public void progress(int pro) {
        if (llProgress.getVisibility() != View.VISIBLE) {
            llProgress.setVisibility(View.VISIBLE);
        }
        if (pro <= bar.getProgress()) {
            return;
        }
        bar.setProgress(pro);
        tvProgress.setText(Math.min(pro, 100) + "%");
        if (needCompose && pro == 100) {
            tvProgress.setText("合成中");
        }
    }

    public void setForceUpdate() {
        findViewById(R.id.bt_close).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.bt_cancel)).setText(R.string.uu_app_exit);
    }

    public void setNoNeedProgress() {
        findViewById(R.id.ll_progress).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.bt_sure)).setText(R.string.uu_app_download);
    }

    public void setNeedCompose() {
        bar.setMax(110);
        needCompose = true;
    }

    public void setError(String msg) {
        if (tvContent != null) {
            if (!TextUtils.isEmpty(msg)) {
                tvContent.setText(msg);
            } else {
                tvContent.setText("下载出错，请重试");
            }
            ((TextView) findViewById(R.id.bt_sure)).setText(R.string.uu_download_retry);
            onceClick = true;
            onceInstall = true;
        }
    }

    public void setDownloadSuccess(String path) {
        ((TextView) findViewById(R.id.bt_sure)).setText(R.string.uu_downloading);
        startInstall(path);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logl.e("installApkActivity onDestroy");
        DefaultActivityController.getInstance().setActivity(null);
    }

    public interface OnDialogClick {

        void onCancel();

        void onSure();

    }
}