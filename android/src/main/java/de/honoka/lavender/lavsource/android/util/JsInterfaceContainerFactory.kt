package de.honoka.lavender.lavsource.android.util

import de.honoka.lavender.lavsource.android.jsinterface.BasicJsInterface
import de.honoka.lavender.lavsource.android.ui.WebActivity
import de.honoka.sdk.util.android.jsinterface.AbstractJavascriptInterfaceContainerFactory
import de.honoka.sdk.util.android.jsinterface.JavascriptInterfaceContainer

class JsInterfaceContainerFactory(private val webActivity: WebActivity) : AbstractJavascriptInterfaceContainerFactory() {

    override val containerInstance: JavascriptInterfaceContainer by lazy {
        JavascriptInterfaceContainer(interfaceInstances, webActivity.webView)
    }

    override val interfaceInstances: List<Any> = listOf(
        BasicJsInterface(webActivity)
    )
}