package de.honoka.lavender.lavsource.android.ui

import de.honoka.lavender.lavsource.android.jsinterface.BilibiliJsInterface
import de.honoka.lavender.lavsource.android.jsinterface.LavsourceJsInterface
import de.honoka.sdk.util.android.ui.AbstractWebActivity

class WebActivity : AbstractWebActivity() {

    override val definedJsInterfaceInstances: List<Any> = listOf(
        LavsourceJsInterface(),
        BilibiliJsInterface()
    )

    override fun extendedOnResume() {}
}