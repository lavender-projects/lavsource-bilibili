package de.honoka.lavender.lavsource.android.util

import android.os.Build
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.sdk.util.android.common.GlobalComponents
import de.honoka.sdk.util.android.common.copyAssetsFileTo
import de.honoka.sdk.util.android.common.launchCoroutineOnIoThread
import de.honoka.sdk.util.android.common.runShellCommandForResult
import de.honoka.sdk.util.android.server.HttpServerUtils
import de.honoka.sdk.util.android.server.HttpServerVariables
import java.io.File
import java.net.ConnectException
import java.util.concurrent.TimeUnit

object LavsourceServerVariables {

    var serverPort = -1

    fun getUrlByPrefix(path: String) = run {
        if(serverPort != -1) "http://localhost:$serverPort$path" else null
    }
}

class LavsourceServer {

    companion object {

        private val pingUrl by lazy {
            LavsourceServerVariables.getUrlByPrefix("/system/ping")
        }

        @Volatile
        var instance: LavsourceServer? = null

        @Volatile
        var initializing: Boolean = false

        private fun createInstance() {
            instance?.let { return }
            synchronized(this) {
                instance?.let { return }
                instance = LavsourceServer()
                initializing = true
            }
            LavsourceServerUtils.initServerPorts()
            instance!!.startProcess()
            initializing = false
        }

        fun isServerRunning(): Boolean {
            instance ?: return false
            return instance!!.checkServerRunningStatus() == null
        }

        fun checkOrRestartInstance() {
            instance ?: run {
                createInstance()
                return
            }
            if(initializing || isServerRunning()) return
            instance!!.stopProcess()
            instance = null
            createInstance()
        }

        fun checkOrRestartInstanceAsync() = launchCoroutineOnIoThread {
            checkOrRestartInstance()
        }
    }

    private var process: Process? = null

    private fun startProcess() {
        File("${GlobalComponents.application.dataDir}/cache/javaTemp").run {
            if(!exists()) mkdirs()
        }
        process = ProcessBuilder(
            "sh", "${GlobalComponents.application.dataDir}/lavsource-server/startup.sh",
            LavsourceServerVariables.serverPort.toString()
        ).apply {
            redirectOutput(File("${GlobalComponents.application.dataDir}/lavsource-server/process.log"))
            redirectErrorStream(true)
        }.start()
        ensureServerRunning()
    }

    private fun stopProcess() = process?.run {
        if(!isAlive) return@run
        destroy()
        var waitSeconds = 0
        while(isAlive && waitSeconds < 5) {
            inputStream.readBytes()
            TimeUnit.SECONDS.sleep(1)
            waitSeconds += 1
        }
        if(isAlive) destroyForcibly()
    }

    private fun checkServerRunningStatus(): Throwable? = run {
        try {
            process!!
            val requestParams = JSONObject().set("serverName", "bilibili")
            val res = HttpUtil.get(pingUrl, requestParams, 1000).let {
                JSONUtil.parseObj(it)
            }
            //status可能为null
            when(res.getBool("status")) {
                true -> null
                false -> Exception(res.getStr("msg"))
                else -> Exception("Unknown")
            }
        } catch(t: Throwable) {
            t
        }
    }

    private fun ensureServerRunning() {
        var connectException: ConnectException? = null
        for(i in 1..20) {
            try {
                checkServerRunningStatus().let {
                    it ?: return
                    throw it
                }
            } catch(t: Throwable) {
                if(t.cause !is ConnectException) throw t
                connectException = t.cause as ConnectException
            }
            TimeUnit.SECONDS.sleep(1)
        }
        throw connectException!!
    }
}

object LavsourceServerUtils {

    /**
     * Termux环境和后端服务相关文件是否已初始化（由其他类维护此变量的值）
     */
    @Volatile
    var allEnvironmentsInitialized = false

    @Synchronized
    fun initTermuxEnvironment() {
        if(File("${GlobalComponents.application.dataDir}/termux").exists()) return
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
            "${GlobalComponents.application.dataDir}/termux-env-openjdk17.zip"
        )
        runShellCommandForResult("unzip ${GlobalComponents.application.dataDir}/termux-env-openjdk17.zip " +
            "-d ${GlobalComponents.application.dataDir}")
    }

    @Synchronized
    fun initLavsourceServer() {
        File("${GlobalComponents.application.dataDir}/lavsource-server/lavsource-server.jar").run {
            if(exists()) return@run
            copyAssetsFileTo(
                "lavsource-server/lavsource-server.jar",
                absolutePath
            )
            copyAssetsFileTo(
                "lavsource-server/startup.sh",
                "${GlobalComponents.application.dataDir}/lavsource-server/startup.sh"
            )
        }
        LavsourceServer.checkOrRestartInstance()
    }

    fun initServerPorts() {
        LavsourceServerVariables.serverPort = HttpServerUtils.getOneAvaliablePort(
            HttpServerVariables.serverPort + 1
        )
    }
}