package de.honoka.lavender.lavsource.android.util

import cn.hutool.http.HttpUtil
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import de.honoka.sdk.util.android.common.GlobalComponents
import de.honoka.sdk.util.android.server.HttpServerUtils
import java.io.File
import java.net.ConnectException
import java.util.concurrent.TimeUnit

object LavsourceServerVariables {

    var lavsourceServerPort = 38082

    fun getUrlByPrefix(path: String) = "http://localhost:$lavsourceServerPort$path"
}

class LavsourceServer(private val port: Int = LavsourceServerVariables.lavsourceServerPort) {

    companion object {

        lateinit var instance: LavsourceServer

        fun createInstance() {
            instance = LavsourceServer().apply { startProcess() }
        }

        fun checkOrRestartInstance() {
            //todo
        }
    }

    var process: Process? = null

    fun startProcess() {
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

    private fun ensureServerRunning() {
        val pingUrl = LavsourceServerVariables.getUrlByPrefix("/system/ping")
        var connectException: ConnectException? = null
        for(i in 1..20) {
            try {
                val res = HttpUtil.get(pingUrl, JSONObject().fluentPut("serverName", "bilibili")).let {
                    JSON.parseObject(it)
                }
                //status可能为null
                when(res.getBoolean("status")) {
                    true -> return
                    false -> throw Exception(res.getString("msg"))
                    else -> {}
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
        LavsourceServerVariables.lavsourceServerPort = HttpServerUtils.getOneAvaliablePort(
            LavsourceServerVariables.lavsourceServerPort + 1
        )
    }
}