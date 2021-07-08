package com.thjolin.install;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

/**
 * Created by tanghao on 2021/6/22
 */
public class GoMarketUtil {
    //获取跳转市场的intent
    public static Intent getIntent(Context paramContext) {
        StringBuilder localStringBuilder = new StringBuilder().append("market://details?id=");
        String str = "你的包名";
        localStringBuilder.append(str);
        Uri localUri = Uri.parse(localStringBuilder.toString());
        return new Intent("android.intent.action.VIEW", localUri);
    }

    //直接跳转不判断是否存在市场应用
    public static void start(Context paramContext, String paramString) {
        Uri localUri = Uri.parse(paramString);
        Intent localIntent = new Intent("android.intent.action.VIEW", localUri);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        paramContext.startActivity(localIntent);
    }

    public static boolean judge(Context paramContext, Intent paramIntent) {
        List<ResolveInfo> localList = paramContext.getPackageManager().queryIntentActivities(paramIntent,
                0);
        if ((localList != null) && (localList.size() > 0)) {
            return false;
        } else {
            return true;
        }
    }

    //跳转到指定的市场，例如腾讯应用宝
    public static void goToTencentMarket(Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.setClassName("com.tencent.android.qqdownloader", "com.tencent.pangu.link.LinkProxyActivity");
        context.startActivity(goToMarket);
    }

}