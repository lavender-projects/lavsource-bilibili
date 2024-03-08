package de.honoka.lavender.lavsource.android.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.honoka.lavender.lavsource.android.util.initHttpServer
import de.honoka.lavender.lavsource.bilibili.R
import de.honoka.sdk.util.android.common.GlobalComponents
import de.honoka.sdk.util.android.common.launchCoroutineOnIoThread
import de.honoka.sdk.util.android.server.HttpServerUtils
import de.honoka.sdk.util.android.ui.fullScreenToShow
import de.honoka.sdk.util.android.ui.jumpToWebActivty

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreenToShow()
        setContentView(R.layout.activity_main)
        launchCoroutineOnIoThread {
            //init可能是一个耗时的操作，故在IO线程中执行，防止阻塞UI线程
            initApplication()
            jumpToWebActivty(WebActivity::class.java)
        }
    }

    private fun initApplication() {
        GlobalComponents.application = application
        HttpServerUtils.initHttpServer()
    }
}