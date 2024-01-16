package de.honoka.lavender.datasource.bilibili.service

import cn.hutool.core.bean.BeanUtil
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.lavender.api.data.Comment
import de.honoka.lavender.api.data.UserInfo
import de.honoka.lavender.api.util.toDateOrTimeDistanceString
import de.honoka.lavender.api.util.toStringWithUnit
import de.honoka.lavender.datasource.bilibili.data.BilibiliLoginParams
import de.honoka.lavender.datasource.bilibili.util.BilibiliUtils
import de.honoka.lavender.datasource.bilibili.util.BilibiliUtils.executeAndSaveBiliCookies
import de.honoka.lavender.datasource.bilibili.util.BilibiliUtils.saveBiliCookies
import de.honoka.lavender.datasource.starter.common.PropertiesHolder
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class BilibiliService(
    private val propertiesHolder: PropertiesHolder
) {

    fun getValidateCode(): JSONObject {
        val url = "https://passport.bilibili.com/x/passport-login/captcha?source=main_web"
        return JSONUtil.parseObj(HttpUtil.get(url)).getJSONObject("data")
    }

    fun login(params: BilibiliLoginParams) {
        /*
         * 先访问bilibili主站，拿到其会预先保存的一些cookie
         *
         * hutool会自动把每次请求所得到的响应当中的cookie，自动保存在全局范围内，之后对某些网站进行请求时，
         * hutool会自动根据保存的cookie列表，并根据cookie的适用域名、过期时间等，自动为即将发起的请求附带
         * 符合条件的cookie，不必手动添加
         *
         * 在之后的请求当中，如果不附带这些预先保存的cookie，那么服务器将会提示登录有安全风险，而无法登录
         */
        HttpUtil.createGet("https://www.bilibili.com").executeAndSaveBiliCookies()
        val getKeyUrl = "https://passport.bilibili.com/x/passport-login/web/key"
        val (key, hash) = JSONUtil.parseObj(HttpUtil.get(getKeyUrl)).run {
            arrayOf(
                getByPath("data.key") as String,
                getByPath("data.hash") as String,
            )
        }
        params.password = BilibiliUtils.encryptPassword(params.password!!, key, hash)
        val loginUrl = "https://passport.bilibili.com/x/passport-login/web/login"
        val response = HttpUtil.createPost(loginUrl).run {
            form(BeanUtil.beanToMap(params))
            headerMap(BilibiliUtils.staticRequestHeaders, true)
            header("Authority", "passport.bilibili.com")
            execute()
        }
        val responseBody = JSONUtil.parseObj(response.body())
        if(responseBody.getInt("code") != 0) throw Exception(responseBody.getStr("message"))
        response.saveBiliCookies()
    }

    fun getNavBarData(): JSONObject {
        val url = "https://api.bilibili.com/x/web-interface/nav"
        return BilibiliUtils.requestForJsonObject(url)
    }

    fun getLoginStatus(): JSONObject {
        val navBarData = getNavBarData()
        return JSONObject().also {
            val logined = navBarData.getInt("code") == 0 && navBarData.getByPath("data.isLogin") == true
            it["logined"] = logined
            it["username"] = if(logined) navBarData.getByPath("data.uname") else null
        }
    }

    fun logout() = BilibiliUtils.clearCookies()

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