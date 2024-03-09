package de.honoka.lavender.lavsource.android.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.honoka.lavender.lavsource.bilibili.R
import de.honoka.sdk.util.android.common.launchCoroutineOnIoThread
import de.honoka.sdk.util.android.ui.fullScreenToShow
import de.honoka.sdk.util.android.ui.jumpToWebActivty
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreenToShow()
        setContentView(R.layout.activity_main)
        launchCoroutineOnIoThread {
            //防止白屏
            delay(300)
            jumpToWebActivty(WebActivity::class.java)
        }
    }
}