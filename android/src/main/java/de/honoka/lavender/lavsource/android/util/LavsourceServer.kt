package de.honoka.lavender.lavsource.android.util

import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.sdk.util.android.common.GlobalComponents
import de.honoka.sdk.util.android.common.launchCoroutineOnIoThread
import de.honoka.sdk.util.android.server.HttpServerUtils
import de.honoka.sdk.util.android.server.HttpServerVariables
import java.io.File
import java.net.ConnectException
import java.util.concurrent.TimeUnit

object LavsourceServerVariables {

    var serverPort = -1

    fun getUrlByPrefix(path: String) = "http://localhost:$serverPort$path"
}

class LavsourceServer(private val port: Int = LavsourceServerVariables.serverPort) {

    companion object {

        private val pingUrl by lazy {
            LavsourceServerVariables.getUrlByPrefix("/system/ping")
        }

        @Volatile
        var instance: LavsourceServer? = null

        private fun createInstance() {
            LavsourceServerUtils.initServerPorts()
            instance?.let { return }
            instance = LavsourceServer()
            instance!!.startProcess()
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
            if(isServerRunning()) return
            instance!!.stopProcess()
            instance!!.startProcess()
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
            "sh", "${GlobalComponents.application.dataDir}/lavsource-server/startup.sh", port.toString()
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
            val res = HttpUtil.get(pingUrl, JSONObject().set("serverName", "bilibili")).let {
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

    fun initServerPorts() {
        LavsourceServerVariables.serverPort = HttpServerUtils.getOneAvaliablePort(
            HttpServerVariables.serverPort + 1
        )
    }
}