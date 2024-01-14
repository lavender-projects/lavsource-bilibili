package de.honoka.lavender.datasource.bilibili.util

import cn.hutool.core.codec.Base64
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IoUtil
import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpResponse
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.lavender.api.data.Comment
import de.honoka.lavender.api.data.UserInfo
import de.honoka.lavender.api.util.toDateOrTimeDistanceString
import de.honoka.lavender.api.util.toStringWithUnit
import de.honoka.lavender.datasource.starter.common.ApplicationContextHolder
import de.honoka.lavender.datasource.starter.common.PropertiesHolder
import de.honoka.sdk.util.file.FileUtils
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
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

    private val cookiesFile = Path(
        FileUtils.getClasspath(), "data", "platform", "bilibili", "cookies.json"
    ).toFile().apply {
        if(exists()) return@apply
        FileUtil.touch(this)
        writeText("{}")
    }

    private val propertiesHolder by lazy {
        ApplicationContextHolder.getBean<PropertiesHolder>()
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

    fun HttpRequest.executeAndSaveBiliCookies(): HttpResponse = execute().also {
        val cookiesArr = JSONUtil.parseArray(JSONUtil.toJsonPrettyStr(it.cookies))
        cookiesArr.forEach { cookie ->
            val parts = (cookie as String).split("=")
            cookies[parts[0]] = parts[1]
        }
        cookiesFile.writeText(cookies.toStringPretty())
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

    fun getProxiedImageUrl(url: String) = run {
        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8)
        "${propertiesHolder.serverAccessUrlPrefix}/platform/bilibili/image/proxy?url=$encodedUrl"
    }

    fun parseComment(json: JSONObject): Comment = Comment().apply {
        id = json.getLong("rpid")
        sender = UserInfo().apply {
            id = json.getByPath("member.mid", String::class.java).toLong()
            name = json.getByPath("member.uname") as String
            avatar = json.getByPath("member.avatar", String::class.java).run {
                getProxiedImageUrl(this)
            }
            level = json.getByPath("member.level_info.current_level") as Int
            ipLocation = json.getByPath("reply_control.location", String::class.java).run {
                if(this?.isEmpty() != false) return@run "未知"
                split("：").run {
                    if(size >= 2) this[1] else this[0]
                }
            }
        }
        sendDate = json.getLong("ctime").toDateOrTimeDistanceString()
        content = run {
            var result = json.getByPath("content.message", String::class.java)
                .replace("<", "&lt;")
                .replace(">", "&gt;")
            val emote = json.getByPath("content.emote", JSONObject::class.java)
            emote?.keys?.forEach {
                val url = emote.getJSONObject(it).getStr("url").run {
                    getProxiedImageUrl(this)
                }
                val size = emote.getJSONObject(it).getByPath("meta.size", Int::class.java)
                val sizeClass = "comment-emoticon-size-$size"
                result = result.replace(it, "<img class=\"comment-emoticon ${sizeClass}\" src=\"${url}\" />")
            }
            result
        }
        likeCount = json.getInt("like").toStringWithUnit()
        if(json.getInt("root") != 0) return@apply
        val previewReplyList = ArrayList<Comment>().also { previewReplyList = it }
        json.getJSONArray("replies").forEach {
            previewReplyList.add(parseComment(it as JSONObject))
        }
        replyCount = json.getInt("rcount")
        replyCountStr = (replyCount ?: 0).toStringWithUnit()
    }
}