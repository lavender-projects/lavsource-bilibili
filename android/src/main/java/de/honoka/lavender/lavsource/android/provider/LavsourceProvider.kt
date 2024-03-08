package de.honoka.lavender.lavsource.android.provider

import de.honoka.lavender.android.lavsource.sdk.provider.AbstractLavsourceProvider
import de.honoka.lavender.lavsource.android.business.BasicBusinessImpl
import de.honoka.lavender.lavsource.android.business.VideoBusinessImpl

class LavsourceProvider : AbstractLavsourceProvider() {

    override fun newBusinessList(): List<Any> = listOf(
        BasicBusinessImpl(),
        VideoBusinessImpl()
    )
}