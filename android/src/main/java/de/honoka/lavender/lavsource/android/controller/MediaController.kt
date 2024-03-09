package de.honoka.lavender.lavsource.android.controller

import cn.hutool.http.HttpResponse
import cn.hutool.http.HttpUtil
import de.honoka.lavender.android.lavsource.sdk.controller.AbstractMediaController
import de.honoka.lavender.lavsource.android.business.VideoBusinessImpl
import de.honoka.lavender.lavsource.android.util.BilibiliUtils.executeWithBiliCookies
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*

object MediaController : AbstractMediaController() {

    override fun getImageResponse(call: ApplicationCall): HttpResponse = run {
        HttpUtil.createGet(call.parameters["url"]).executeWithBiliCookies()
    }

    override fun getVideoResponse(call: ApplicationCall): HttpResponse = run {
        VideoBusinessImpl.getVideoStreamResponse(call.parameters["url"]!!, call.request.header(HttpHeaders.Range))
    }
}