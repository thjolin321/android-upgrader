package com.thjolin.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.thjolin.update.R;
import com.thjolin.download.util.Logl;
import com.thjolin.download.util.Utils;

/**
 * Created by th on 2021/7/12
 */
public class PDialog extends Dialog implements View.OnClickListener {

    private OnDialogClick onDialogClick;
    private ProgressBar bar;
    private TextView tvProgress, tvSpeed, tvTitle;
    private View mView;
    private View llProgress;
    private boolean needCompose;

    public PDialog(@NonNull Context context) {
        super(context, R.style.UuCustomDialog);
        Logl.e("PDialog");
        setCustomDialog();
    }

    private void setCustomDialog() {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.progress_tang_update, null);
        mView.findViewById(R.id.bt_cancel).setOnClickListener(this);
        mView.findViewById(R.id.bt_close).setOnClickListener(this);
        mView.findViewById(R.id.bt_sure).setOnClickListener(this);
        bar = mView.findViewById(R.id.progress_bar);
        tvProgress = mView.findViewById(R.id.tv_progress);
        tvTitle = mView.findViewById(R.id.dialog_title);
        llProgress = mView.findViewById(R.id.ll_progress);
        tvSpeed = mView.findViewById(R.id.tv_speed);
        super.setContentView(mView);
    }

    @Override
    public void show() {
        super.show();
        setSize();
    }

    public void setSize() {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (int) (Utils.getScreenWidth() * 0.8); //设置宽度
        this.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt_sure) {
            if (onDialogClick != null) {
                onDialogClick.onSure();
            }
        } else if (id == R.id.bt_cancel) {
            if (onDialogClick != null) {
                onDialogClick.onCancel();
            }
        }
//        dismiss();
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

    public void showSpeed(String speed) {
        if (tvSpeed.getVisibility() != View.VISIBLE) {
            tvSpeed.setVisibility(View.VISIBLE);
        }
        tvSpeed.setText(speed);
    }

    public void setForceUpdate() {
        findViewById(R.id.bt_close).setVisibility(View.GONE);
        ((TextView) mView.findViewById(R.id.bt_cancel)).setText(R.string.uu_app_exit);
    }

    public void setNoNeedProgress() {
        mView.findViewById(R.id.ll_progress).setVisibility(View.GONE);
        ((TextView) mView.findViewById(R.id.bt_sure)).setText(R.string.uu_app_download);
    }

    public void setNeedCompose() {
        bar.setMax(110);
        needCompose = true;
    }

    public void showGotoMarketTitle() {
        tvTitle.setText("发现新版本，请前往应用市场下载安装");
    }


    public interface OnDialogClick {

        void onCancel();

        void onSure();

    }

}