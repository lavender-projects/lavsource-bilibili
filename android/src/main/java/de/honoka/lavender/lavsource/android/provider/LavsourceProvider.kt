package de.honoka.lavender.lavsource.android.provider

import de.honoka.lavender.android.lavsource.sdk.provider.AbstractLavsourceProvider
import de.honoka.lavender.lavsource.android.business.BasicBusinessImpl
import de.honoka.lavender.lavsource.android.business.VideoBusinessImpl
import de.honoka.lavender.lavsource.android.util.initHttpServer
import de.honoka.sdk.util.android.server.HttpServerUtils

class LavsourceProvider : AbstractLavsourceProvider() {

    override fun onCreate(): Boolean = super.onCreate().apply {
        HttpServerUtils.initHttpServer()
    }

    override fun newBusinessList(): List<Any> = listOf(
        BasicBusinessImpl(),
        VideoBusinessImpl()
    )
}