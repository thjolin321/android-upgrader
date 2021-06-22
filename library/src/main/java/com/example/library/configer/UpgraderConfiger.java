package com.example.library.configer;

import android.app.Dialog;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tanghao on 2021/6/11
 */
public class UpgraderConfiger {

    public final static int PATCH_UPDATE = 1;
    public final static int COMPLETE_UPDATE = 2;
    public final static int MARKET_UPDATE = 3;
    public final static int HOT_UPDATE = 4;

    private boolean showDownladProgress;
    private boolean silent;
    private boolean forceUpdate;
    private Dialog updateDialog;
    private int updateMethod; // 0 for no value,-1 for no need update, 1、for patch,
    // 2 for completeApk, 3 for market, 4 for 插件化更新, -2 for wrong type

    @IntDef({PATCH_UPDATE, COMPLETE_UPDATE, MARKET_UPDATE, HOT_UPDATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UpdateMethod {
    }


    public UpgraderConfiger(boolean showDownladProgress, boolean silent, boolean forceUpdate, Dialog updateDialog, int updateMethod) {
        this.showDownladProgress = showDownladProgress;
        this.silent = silent;
        this.forceUpdate = forceUpdate;
        this.updateDialog = updateDialog;
        this.updateMethod = updateMethod;
    }

    public static class Builder {

        private boolean silent;
        private boolean showDownladProgress;
        private boolean forceUpdate;
        private Dialog updateDialog;
        private int updateMethod; // 0 for no need update, 1、for patch, 2 for completeApk, 3 for marcket, 4 for 插件化更新, -1 for wrong type

        public Builder silent(boolean silent) {
            this.silent = silent;
            return this;
        }

        public Builder showDownladProgress(boolean showDownladProgress) {
            this.showDownladProgress = showDownladProgress;
            return this;
        }

        public Builder forceUpdate(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
            return this;
        }

        public Builder updateDialog(@NonNull Dialog updateDialog) {
            this.updateDialog = updateDialog;
            return this;
        }

        public Builder updateMethod(@UpdateMethod int updateMethod) {
            this.updateMethod = updateMethod;
            return this;
        }

        public UpgraderConfiger build() {
            return new UpgraderConfiger(showDownladProgress, silent, forceUpdate, updateDialog, updateMethod);
        }
    }


}