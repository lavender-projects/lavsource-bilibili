package de.honoka.lavender.lavsource.android.util

import de.honoka.lavender.android.lavsource.sdk.util.ApplicationUtils
import de.honoka.lavender.android.lavsource.sdk.util.LavsourceUtilsAbstractPartImpl
import de.honoka.lavender.api.util.LavsourceUtils
import de.honoka.lavender.lavsource.android.controller.MediaController
import de.honoka.lavender.lavsource.bilibili.business.util.BilibiliUtils
import de.honoka.sdk.util.android.server.HttpServer

object ApplicationUtilsAbstractPartImpl : ApplicationUtils.AbstractPart {

    override fun initHttpServer() {
        HttpServer.customRoutingList = listOf(
            MediaController.routingDefinition
        )
        HttpServer.checkOrRestartInstance()
    }

    override fun initPartialAbstractObjects() {
        BilibiliUtils.initAbstractPart(BilibiliUtilsAbstractPartImpl)
        LavsourceUtils.initAbstractPart(LavsourceUtilsAbstractPartImpl)
    }
}