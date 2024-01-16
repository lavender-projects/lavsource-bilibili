package de.honoka.lavender.lavsource.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import de.honoka.lavender.lavsource.bilibili.R
import de.honoka.lavender.lavsource.util.GlobalData
import de.honoka.lavender.lavsource.util.LavsourceServer
import de.honoka.lavender.lavsource.util.ServerUtils
import de.honoka.lavender.lavsource.util.WebServer
import de.honoka.lavender.lavsource.util.copyAssetsFileTo
import de.honoka.lavender.lavsource.util.runShellCommandForResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

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
        GlobalData.application = application
        initTermuxEnvironment()
        initWebServer()
        initLavsourceServer()
    }

    private fun initTermuxEnvironment() {
        if(File("${application.dataDir}/termux").exists()) return
        var cpuArchitecture = Build.SUPPORTED_64_BIT_ABIS.run {
            if(isEmpty()) null else Build.SUPPORTED_64_BIT_ABIS[0].lowercase()
        }
        cpuArchitecture = when(cpuArchitecture) {
            "aarch64", "arm64", "arm64-v8a" -> "arm64"
            "x86_64", "x64", "amd64" -> "x64"
            else -> throw Exception("不支持的CPU架构")
        }
        copyAssetsFileTo(
            "termux/termux-env-openjdk17-${cpuArchitecture}.zip",
            "${application.dataDir}/termux-env-openjdk17.zip"
        )
        runShellCommandForResult("unzip ${application.dataDir}/termux-env-openjdk17.zip -d ${application.dataDir}")
    }

    private fun initWebServer() {
        ServerUtils.initServerPorts()
        WebServer.createInstance()
    }

    private fun initLavsourceServer() {
        File("${application.dataDir}/lavsource-server/lavsource-server.jar").run {
            if(exists()) return@run
            copyAssetsFileTo(
                "lavsource-server/lavsource-server.jar",
                absolutePath
            )
            copyAssetsFileTo(
                "lavsource-server/startup.sh",
                "${application.dataDir}/lavsource-server/startup.sh"
            )
        }
        LavsourceServer.createInstance()
    }

    private fun jumpToWebActivty() = runOnUiThread {
        startActivity(Intent(this, WebActivity::class.java).apply {
            putExtra("firstWebActivity", true)
        })
        finish()
    }
}