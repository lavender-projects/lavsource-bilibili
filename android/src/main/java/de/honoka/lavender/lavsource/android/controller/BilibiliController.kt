package de.honoka.lavender.lavsource.android.controller

import cn.hutool.json.JSONUtil
import de.honoka.lavender.lavsource.android.business.BilibiliBusiness
import de.honoka.lavender.lavsource.android.data.BilibiliLoginParams
import de.honoka.sdk.util.android.server.KtorCodeUtils
import de.honoka.sdk.util.android.server.respondJson
import de.honoka.sdk.util.framework.web.ApiResponse
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

val bilibiliController = KtorCodeUtils.getNestedApiRoutingDefinition("/platform/bilibili") {

    get("/validateCode") {
        call.respondJson(ApiResponse.success(BilibiliBusiness.getValidateCode()))
    }

    post("/login") {
        val params = JSONUtil.toBean(call.receiveText(), BilibiliLoginParams::class.java)
        BilibiliBusiness.login(params)
        call.respondJson(ApiResponse.success())
    }

    get("/loginStatus") {
        call.respondJson(ApiResponse.success(BilibiliBusiness.getLoginStatus()))
    }

    get("/logout") {
        BilibiliBusiness.logout()
        call.respondJson(ApiResponse.success())
    }
}