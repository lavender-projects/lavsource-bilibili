package de.honoka.lavender.lavsource.bilibili.util

import cn.hutool.core.codec.Base64
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IoUtil
import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpResponse
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import kotlin.io.path.Path

object BilibiliUtils {

    val staticRequestHeaders: Map<String, String> by lazy {
        val jsonStr = IoUtil.readUtf8(javaClass.getResource("/bilibili/staticRequestHeaders.json")!!.openStream())
        val json = JSONUtil.parseObj(jsonStr)
        HashMap<String, String>().apply {
            json.entries.forEach {
                this[it.key] = if(it.value is String) it.value as String else it.value.toString()
            }
        }
    }

    private val cookies by lazy {
        JSONUtil.parseObj(cookiesFile.readText())
    }

    private val cookiesFile = Path(EnvironmentPathUtils.dataDirPathOfApp, "data/cookies.json").toFile().apply {
        if(exists()) return@apply
        FileUtil.touch(this)
        writeText("{}")
    }

    fun encryptPassword(password: String, rawKey: String, hash: String): String {
        val key = StringBuilder().apply {
            rawKey.lines().let {
                for(i in 1..it.size - 2) {
                    append(it[i])
                }
            }
        }.toString()
        val cipher = KeyFactory.getInstance("RSA").run {
            Cipher.getInstance(algorithm).apply {
                init(Cipher.PUBLIC_KEY, generatePublic(X509EncodedKeySpec(Base64.decode(key))))
            }
        }
        return Base64.encode(cipher.doFinal("${hash}${password}".toByteArray()))
    }

    /**
     * 将存放在json中的所有cookie键值对，拼接成cookie请求头需要的格式
     */
    private fun readBiliCookiesString(): String = StringBuilder().run {
        cookies.keys.forEach {
            append("$it=${cookies.getStr(it)}; ")
        }
        val res = toString()
        if(!res.contains(";")) res
        else res.substring(0, res.lastIndexOf(";"))
    }

    fun HttpRequest.executeAndSaveBiliCookies(): HttpResponse = execute().saveBiliCookies()

    fun HttpResponse.saveBiliCookies(): HttpResponse {
        val cookiesArr = JSONUtil.parseArray(JSONUtil.toJsonPrettyStr(cookies))
        cookiesArr.forEach { cookie ->
            val parts = (cookie as String).split("=")
            BilibiliUtils.cookies[parts[0]] = parts[1]
        }
        cookiesFile.writeText(BilibiliUtils.cookies.toStringPretty())
        return this
    }

    fun HttpRequest.addBiliCookies() {
        cookie(readBiliCookiesString())
    }

    fun HttpRequest.executeWithBiliCookies(): HttpResponse {
        addBiliCookies()
        return execute()
    }

    fun danmakuTypeToString(type: Int): String = when(type) {
        1, 2, 3 -> "scroll"
        4 -> "bottom"
        5 -> "top"
        else -> "scroll"
    }

    fun requestForJsonObject(url: String, method: String = "get", withCookies: Boolean = true): JSONObject {
        val request = when(method.lowercase()) {
            "get" -> HttpUtil.createGet(url)
            "post" -> HttpUtil.createPost(url)
            else -> throw Exception("未知的请求方式")
        }
        val response = if(withCookies) {
            request.executeWithBiliCookies()
        } else {
            request.execute()
        }
        return JSONUtil.parseObj(response.body())
    }

    fun bvIdToAvId(bvId: String): Long {
        val url = "https://api.bilibili.com/x/web-interface/view?bvid=$bvId"
        val json = requestForJsonObject(url)
        return (json.getByPath("data.aid") as Number).toLong()
    }

    fun clearCookies() {
        cookies.clear()
        cookiesFile.writeText("{}")
    }
}