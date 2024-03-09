@file:Suppress("UnusedReceiverParameter")

package de.honoka.lavender.lavsource.android.util

import de.honoka.lavender.lavsource.android.controller.MediaController
import de.honoka.lavender.lavsource.android.controller.bilibiliController
import de.honoka.sdk.util.android.server.HttpServer
import de.honoka.sdk.util.android.server.HttpServerUtils

fun HttpServerUtils.initHttpServer() {
    HttpServer.customRoutingList = listOf(
        bilibiliController,
        MediaController.routingDefinition
    )
    HttpServer.checkOrRestartInstance()
}