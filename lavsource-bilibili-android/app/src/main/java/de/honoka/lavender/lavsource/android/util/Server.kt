package de.honoka.lavender.lavsource.android.util

import android.webkit.MimeTypeMap
import cn.hutool.http.HttpUtil
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.File
import java.net.ConnectException
import java.util.concurrent.TimeUnit

object ServerVariables {

    var webServerPort = 38081

    var lavsourceServerPort = 38082

    fun getUrlByWebServerPrefix(path: String) = "http://localhost:$webServerPort$path"

    fun getUrlByLavsourceServerPrefix(path: String) = "http://localhost:$lavsourceServerPort$path"
}

class WebServer(port: Int = ServerVariables.webServerPort) : NanoHTTPD(port) {

    companion object {

        lateinit var instance: WebServer

        private val staticResourcesPrefixes = arrayOf(
            "/assets", "/font", "/img", "/js", "/favicon.ico"
        )

        fun createInstance() {
            instance = WebServer().apply { start() }
        }

        fun checkOrRestartInstance() {
            if(instance.isAlive) return
            instance.start()
        }
    }

    override fun serve(session: IHTTPSession): Response {
        var path = session.uri
        if(path == "/") path = "/index.html"
        return handle(path)
    }

    private fun handle(path: String): Response {
        //判断路径前缀
        staticResourcesPrefixes.forEach {
            //加载静态资源
            if(path.startsWith(it)) return staticResourceResponse(path)
        }
        //加载index.html
        return indexHtmlResponse()
    }

    private fun staticResourceResponse(path: String): Response {
        val content = GlobalData.application.assets.open("web$path").use { it.readBytes() }
        val fileExt = path.substring(path.lastIndexOf(".") + 1)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt)
        return newFixedLengthResponse(Response.Status.OK, mimeType, ByteArrayInputStream(content), content.size.toLong())
    }

    private fun indexHtmlResponse(): Response {
        val content = GlobalData.application.assets.open("web/index.html").use { it.readBytes() }
        return newFixedLengthResponse(Response.Status.OK, MIME_HTML, ByteArrayInputStream(content), content.size.toLong())
    }
}

class LavsourceServer(private val port: Int = ServerVariables.lavsourceServerPort) {

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
        File("${GlobalData.application.dataDir}/cache/javaTemp").run {
            if(!exists()) mkdirs()
        }
        process = ProcessBuilder(
            "sh", "${GlobalData.application.dataDir}/lavsource-server/startup.sh", port.toString()
        ).apply {
            redirectOutput(File("${GlobalData.application.dataDir}/lavsource-server/process.log"))
            redirectErrorStream(true)
        }.start()
        ensureServerRunning()
    }

    private fun ensureServerRunning() {
        val pingUrl = ServerVariables.getUrlByLavsourceServerPrefix("/system/ping")
        var connectException: ConnectException? = null
        for(i in 1..10) {
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

object ServerUtils {

    private fun getOneAvaliablePort(startPort: Int): Int {
        var port = startPort
        var successful = false
        //验证端口可用性
        for(i in 0 until 10) {
            try {
                WebServer(port).apply {
                    start()
                    stop()
                }
                successful = true
                break
            } catch(t: Throwable) {
                port += 1
            }
        }
        if(!successful) throw RuntimeException("端口范围（$startPort - ${startPort + 10}）均被占用")
        return port
    }

    fun initServerPorts() {
        ServerVariables.webServerPort = getOneAvaliablePort(ServerVariables.webServerPort)
        ServerVariables.lavsourceServerPort = getOneAvaliablePort(ServerVariables.webServerPort + 1)
    }
}