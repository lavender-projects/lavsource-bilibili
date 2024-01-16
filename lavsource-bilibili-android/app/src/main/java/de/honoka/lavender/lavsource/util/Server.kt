package de.honoka.lavender.lavsource.util

import android.webkit.MimeTypeMap
import cn.hutool.http.HttpUtil
import com.alibaba.fastjson2.JSON
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
        val params = HashMap<String, String>()
        session.parameters.forEach {
            params[it.key] = if(it.value.isNotEmpty()) it.value[0] else ""
        }
        var path = session.uri
        if(path == "/") path = "/index.html"
        return handle(path, params)
    }

    private fun handle(path: String, params: Map<String, String>): Response {
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
            val logFile = File("${GlobalData.application.dataDir}/lavsource-server/process.log")
            redirectOutput(logFile)
            redirectError(logFile)
        }.start()
        ensureServerRunning()
    }

    private fun ensureServerRunning() {
        val pingUrl = ServerVariables.getUrlByLavsourceServerPrefix("/system/ping")
        for(i in 1..10) {
            try {
                val res = JSON.parseObject(HttpUtil.get(pingUrl))
                //status可能为null
                if(res.getBoolean("status") == true) break
            } catch(t: Throwable) {
                if(t.cause !is ConnectException) throw t
            }
            TimeUnit.SECONDS.sleep(1)
        }
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