package com.thjolin.install

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageInstaller
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.thjolin.update.R

/**
 * Created by tanghao on 2021/7/5
 */
class AAAutilActivity: FragmentActivity() {

    //intent-filter 的 action
    private val ACTION_INSTALL = "cc.xiaobaicz.work.INSTALL_APK"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tang_permission)

        install()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun install() {
        packageManager.packageInstaller.apply {
            //生成参数
            val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
            //创建ID
            val id = createSession(params)
            //打开Session
            val session = openSession(id)
            //写入文件
            writeAssetsApk2Session(session, "elevato.apk")
            //新建IntentSender
            val intent = createIntentSender()
            //提交，进行安装
            session.commit(intent)
        }
    }

    /**
     * 新建一个IntentSender用于接收结果
     * 该例子通过当前页面接收
     */
    private fun createIntentSender(): IntentSender {
        val intent = Intent(this, AAAutilActivity::class.java).apply {
            action = ACTION_INSTALL
        }
        val pending = PendingIntent.getActivity(this, 0, intent, 0)
        return pending.intentSender
    }

    //写入Apk到Session输出流，该例子 获取Assets内文件，可通过其他方式获取Apk流
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun writeAssetsApk2Session(session: PackageInstaller.Session, s: String) {
        assets.open(s).use { input ->
            session.openWrite("apk", 0, -1).use { output ->
                output.write(input.readBytes())
            }
        }
    }

    /**
     * 接收安装结果
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent ?: return
        if (intent.action != ACTION_INSTALL) {
            return
        }
        intent.extras?.apply {
            when (this.getInt(PackageInstaller.EXTRA_STATUS)) {
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    //提示用户进行安装
                    startActivity(this.get(Intent.EXTRA_INTENT) as Intent)
                }
                PackageInstaller.STATUS_SUCCESS -> {
                    //安装成功
                }
                else -> {
                    //失败信息
                    val msg = this.getString(PackageInstaller.EXTRA_STATUS_MESSAGE)
                }
            }
        }
    }

}