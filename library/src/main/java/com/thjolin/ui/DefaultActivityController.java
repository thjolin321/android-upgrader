package com.thjolin.ui;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.thjolin.install.InstallApkActivity;
import com.thjolin.install.InstallHelper;
import com.thjolin.update.R;
import com.thjolin.update.operate.listener.UiListener;
import com.thjolin.util.Logl;

/**
 * Created by tanghao on 2021/7/1
 */
public class DefaultActivityController implements UiListener {

    PDialog.OnDialogClick onDialogClick;

    public final static String UPGRADE_CHANNEL = "com.thjolin.upgrade";
    public final static int UPGRADE_NOTIFICATION_ID = 0X9527;
    private NotificationChannel mChannel;
    private Notification notification;
    private NotificationManager nm;
    private boolean showNotification;
    private boolean needCompose;
    private InstallApkActivity activity;
    private InstallApkActivity.OnDialogClick onRightClick;

    static DefaultActivityController instance = new DefaultActivityController();

    private DefaultActivityController() {
    }

    public static DefaultActivityController getInstance() {
        return instance;
    }

    public void setActivity(InstallApkActivity activity) {
        this.activity = activity;
        if (activity != null) {
            activity.setOnDialogClick(onRightClick);
        }
    }


    @Override
    public void show(boolean showNotification, boolean forceUpdate, boolean needDownload, boolean needCompose, String apkPath, String fileName) {
        Logl.e("show========");
        InstallHelper.showDialogActivity(showNotification, forceUpdate, needDownload, needCompose, apkPath, fileName);
    }

    @Override
    public void showNotification() {
        if (activity == null) return;
        showNotification = true;
        showNotification(activity, "");
    }

    public void progress(int pro) {
        if (activity == null) return;
        if (showNotification) {
            showNotificationProgress(pro);
            return;
        }
        activity.progress(pro);
    }

    @Override
    public void downloadSuccess(String path) {
        if (activity != null && !activity.isFinishing()) {
            activity.setDownloadSuccess(path);
            InstallHelper.installWithNoActivity(activity, path);
        } else {
            InstallHelper.installWithActivity(path);
        }
    }

    @Override
    public void failed(String msg) {
        if (activity == null) return;
        activity.setError(msg);
    }

    @Override
    public void setOnRightClick(InstallApkActivity.OnDialogClick onRightClick) {
        this.onRightClick = onRightClick;
    }

    public void setOnDialogClick(PDialog.OnDialogClick onDialogClick) {
        this.onDialogClick = onDialogClick;
    }

    /**
     * 显示通知栏
     *
     * @param context 上下文对象
     */
    public void showNotification(Context context, String title) {
        Logl.e("showNotification");
        if (activity == null) return;
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 兼容 8.0 系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context, nm);
        }
        NotificationCompat.Builder builder = createNotificationCompatBuilder(context, title);
        notification = builder.build();
        notification.contentView = new RemoteViews(activity.getApplicationContext()
                .getPackageName(), R.layout.custom_download_notification);
        showNotificationProgress(0);
    }

    public void showNotificationProgress(int progress) {
        notification.contentView.setProgressBar(R.id.progress_bar, 100 + (needCompose ? 10 : 0), progress, false);
        notification.contentView.setTextViewText(R.id.tv_progress, progress + "%");
        if (progress == 100) {
            if (needCompose) {
                notification.contentView.setTextViewText(R.id.tv_progress, "合成中");
            } else {
                nm.cancel(UPGRADE_NOTIFICATION_ID);
            }
        }
        if (progress == 110) {
            nm.cancel(UPGRADE_NOTIFICATION_ID);
        }
        nm.notify(UPGRADE_NOTIFICATION_ID, notification);
    }

    @NonNull
    private NotificationCompat.Builder createNotificationCompatBuilder(Context context, String title) {
        Logl.e("createNotificationCompatBuilder");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, UPGRADE_CHANNEL);
        builder.setSmallIcon(R.drawable.tang_close);
        builder.setContentTitle(title);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        return builder;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(Context context, NotificationManager notificationManager) {
        // 通知渠道
        if (mChannel != null) {
            return;
        }
        mChannel = new NotificationChannel(UPGRADE_CHANNEL, activity.getString(R.string.download_name), NotificationManager.IMPORTANCE_DEFAULT);
        // 开启指示灯，如果设备有的话。
        mChannel.enableLights(false);
        // 开启震动
        mChannel.enableVibration(false);
        mChannel.setSound(null, null);
        // 设置是否应在锁定屏幕上显示此频道的通知
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        // 设置是否显示角标
        mChannel.setShowBadge(false);
        //  设置绕过免打扰模式
        mChannel.setBypassDnd(false);
        //最后在notificationmanager中创建该通知渠道
        notificationManager.createNotificationChannel(mChannel);
    }

}