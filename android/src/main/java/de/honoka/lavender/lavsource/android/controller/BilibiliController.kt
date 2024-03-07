package de.honoka.lavender.lavsource.android.controller

import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONUtil
import de.honoka.lavender.lavsource.android.data.BilibiliLoginParams
import de.honoka.lavender.lavsource.android.util.BilibiliBusinessUtils
import de.honoka.lavender.lavsource.android.util.BilibiliUtils.executeWithBiliCookies
import de.honoka.sdk.util.android.server.KtorCodeUtils
import de.honoka.sdk.util.android.server.respondJson
import de.honoka.sdk.util.framework.web.ApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val bilibiliController = KtorCodeUtils.getNestedRoutingDefinition("/api/platform/bilibili") {

    get("/validateCode") {
        call.respondJson(ApiResponse.success(BilibiliBusinessUtils.getValidateCode()))
    }

    post("/login") {
        val params = JSONUtil.toBean(call.receiveText(), BilibiliLoginParams::class.java)
        BilibiliBusinessUtils.login(params)
        call.respondJson(ApiResponse.success())
    }

    get("/loginStatus") {
        call.respondJson(ApiResponse.success(BilibiliBusinessUtils.getLoginStatus()))
    }

    get("/image/proxy") {
        val url = call.parameters["url"]
        val originalRes = HttpUtil.createGet(url).executeWithBiliCookies()
        call.respondBytes(originalRes.bodyBytes(), ContentType.parse(originalRes.header(HttpHeaders.ContentType)))
    }

    get("/logout") {
        BilibiliBusinessUtils.logout()
        call.respondJson(ApiResponse.success())
    }
}