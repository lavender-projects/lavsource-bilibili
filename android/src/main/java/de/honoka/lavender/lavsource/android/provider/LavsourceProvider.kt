package de.honoka.lavender.lavsource.android.provider

import de.honoka.lavender.android.lavsource.sdk.provider.AbstractLavsourceProvider
import de.honoka.lavender.android.lavsource.sdk.util.ApplicationUtils
import de.honoka.lavender.lavsource.android.business.BasicBusinessImpl
import de.honoka.lavender.lavsource.android.util.ApplicationUtilsAbstractPartImpl
import de.honoka.lavender.lavsource.bilibili.business.business.VideoBusinessImpl

class LavsourceProvider : AbstractLavsourceProvider() {

    override val applicationUtilsAbstractPart: ApplicationUtils.AbstractPart = ApplicationUtilsAbstractPartImpl

    override fun newBusinessList(): List<Any> = listOf(
        BasicBusinessImpl,
        VideoBusinessImpl
    )
}