package com.thjolin.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.thjolin.update.R;
import com.thjolin.util.Logl;
import com.thjolin.util.Utils;

/**
 * Created by tanghao on 2021/7/1
 */
public class ProgressDialog extends DialogFragment {

    PDialog pDialog;
    OnDialogClick onDialogClick;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Logl.e("onCreateDialog");
        pDialog = new PDialog(getActivity());
        pDialog.setOnDialogClick(onDialogClick);
        return pDialog;
    }

    public void progress(int pro) {
        if (pDialog == null) return;
        pDialog.progress(pro);
    }

    public void setOnDialogClick(OnDialogClick onDialogClick) {
        this.onDialogClick = onDialogClick;
    }


    static class PDialog extends Dialog implements View.OnClickListener {

        private OnDialogClick onDialogClick;
        private ProgressBar bar;

        public PDialog(@NonNull Context context) {
            super(context, R.style.TangCustomDialog);
            Logl.e("PDialog");
            setCustomDialog();
        }

        private void setCustomDialog() {
            View mView = LayoutInflater.from(getContext()).inflate(R.layout.progress_tang_update, null);
            bar = mView.findViewById(R.id.progress_bar);
            mView.findViewById(R.id.bt_cancel).setOnClickListener(this);
            mView.findViewById(R.id.bt_close).setOnClickListener(this);
            mView.findViewById(R.id.bt_sure).setOnClickListener(this);
            super.setContentView(mView);
        }

        public void progress(int pro) {
            if (pro <= bar.getProgress()) {
                return;
            }
            bar.setProgress(pro);
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
            dismiss();
        }

        public void setOnDialogClick(OnDialogClick onDialogClick) {
            this.onDialogClick = onDialogClick;
        }

    }

    public interface OnDialogClick {

        void onCancel();

        void onSure();

    }

}