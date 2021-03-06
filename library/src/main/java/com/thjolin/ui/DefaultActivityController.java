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
import com.thjolin.download.util.Logl;

import java.io.File;

/**
 * Created by th on 2021/7/1
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
        if (showNotification) {
            showNotificationProgress(pro);
            return;
        }
        if (activity == null) return;
        activity.progress(pro);
    }

    @Override
    public void downloadSuccess(String path) {
        if (activity != null && !activity.isFinishing()) {
            activity.setDownloadSuccess(path);
            InstallHelper.installApkWithIntent(activity, new File(path));
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
     * ???????????????
     *
     * @param context ???????????????
     */
    public void showNotification(Context context, String title) {
        Logl.e("showNotification");
        if (activity == null) return;
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // ?????? 8.0 ??????
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
        Logl.e("?????????????????????" + progress);
        notification.contentView.setProgressBar(R.id.progress_bar, 100 + (needCompose ? 10 : 0), progress, false);
        notification.contentView.setTextViewText(R.id.tv_progress, progress + "%");
        if (progress == 100) {
            if (needCompose) {
                notification.contentView.setTextViewText(R.id.tv_progress, "?????????");
            } else {
                nm.cancel(UPGRADE_NOTIFICATION_ID);
            }
        }
        if (progress == 110) {
            nm.cancel(UPGRADE_NOTIFICATION_ID);
        }
        nm.notify(UPGRADE_NOTIFICATION_ID, notification);
        Logl.e("?????????????????????");
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
        // ????????????
        if (mChannel != null) {
            return;
        }
        mChannel = new NotificationChannel(UPGRADE_CHANNEL, activity.getString(R.string.uu_download_name), NotificationManager.IMPORTANCE_DEFAULT);
        // ??????????????????????????????????????????
        mChannel.enableLights(false);
        // ????????????
        mChannel.enableVibration(false);
        mChannel.setSound(null, null);
        // ?????????????????????????????????????????????????????????
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        // ????????????????????????
        mChannel.setShowBadge(false);
        // ??????????????????????????????
        mChannel.setBypassDnd(false);
        //?????????notificationmanager????????????????????????
        notificationManager.createNotificationChannel(mChannel);
    }

}