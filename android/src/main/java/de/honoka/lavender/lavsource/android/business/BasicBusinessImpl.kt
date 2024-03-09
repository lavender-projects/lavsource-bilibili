package de.honoka.lavender.lavsource.android.business

import cn.hutool.json.JSONObject
import de.honoka.lavender.api.business.BasicBusiness
import de.honoka.sdk.util.android.server.HttpServer

object BasicBusinessImpl : BasicBusiness {

    override fun statusCheck(): JSONObject {
        HttpServer.checkOrRestartInstance()
        return JSONObject().set("status", HttpServer.instance.isActive)
    }
}