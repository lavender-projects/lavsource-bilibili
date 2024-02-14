package de.honoka.lavender.lavsource.android.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import de.honoka.lavender.lavsource.android.util.LavsourceServerUtils
import de.honoka.lavender.lavsource.bilibili.R
import de.honoka.sdk.util.android.common.GlobalComponents
import de.honoka.sdk.util.android.server.HttpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreenToShow()
        setContentView(R.layout.activity_main)
        CoroutineScope(Dispatchers.IO).launch {
            //init可能是一个耗时的操作，故在IO线程中执行，防止阻塞UI线程
            initApplication()
            jumpToWebActivty()
        }
    }

    /**
     * 全屏化当前Activity
     */
    @Suppress("DEPRECATION")
    private fun fullScreenToShow() {
        //隐藏状态栏（手机时间、电量等信息显示的地方）
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //隐藏虚拟按键
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private fun initApplication() {
        GlobalComponents.application = application
        HttpServer.createInstance()
        if(LavsourceServerUtils.allEnvironmentsInitialized) return
        LavsourceServerUtils.initTermuxEnvironment()
        LavsourceServerUtils.initLavsourceServer()
        LavsourceServerUtils.allEnvironmentsInitialized = true
    }

    private fun jumpToWebActivty() = runOnUiThread {
        startActivity(Intent(this, WebActivity::class.java).apply {
            putExtra("firstWebActivity", true)
        })
        finish()
    }
}