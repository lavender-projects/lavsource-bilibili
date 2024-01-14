package de.honoka.lavender.datasource.bilibili.service

import cn.hutool.core.bean.BeanUtil
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.lavender.datasource.bilibili.data.BilibiliLoginParams
import de.honoka.lavender.datasource.bilibili.util.BilibiliUtils
import de.honoka.lavender.datasource.bilibili.util.BilibiliUtils.executeAndSaveBiliCookies
import org.springframework.stereotype.Service

@Service
class BilibiliService {

    fun validateCode(): JSONObject {
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
        HttpUtil.createPost(loginUrl).run {
            form(BeanUtil.beanToMap(params))
            headerMap(BilibiliUtils.staticRequestHeaders, true)
            header("Authority", "passport.bilibili.com")
            executeAndSaveBiliCookies()
        }
    }
}