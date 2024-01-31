package de.honoka.lavender.lavsource.android.jsinterface

import de.honoka.lavender.lavsource.android.jsinterface.async.AsyncTaskJsInterface
import de.honoka.lavender.lavsource.android.jsinterface.definition.BasicJsInterface
import de.honoka.lavender.lavsource.android.jsinterface.definition.LavsourceServerJsInterface
import de.honoka.lavender.lavsource.android.ui.WebActivity

class JavaScriptInterfaces(private val webActivity: WebActivity) {

    private val interfaceInstances = arrayListOf(
        BasicJsInterface(webActivity),
        LavsourceServerJsInterface()
    )

    val interfaces = HashMap<String, Any>().apply {
        interfaceInstances.add(AsyncTaskJsInterface(this@JavaScriptInterfaces, webActivity))
        interfaceInstances.forEach {
            this[it.javaClass.simpleName] = it
        }
    }

    init {
        registerJsInterfaces()
    }

    private fun registerJsInterfaces() {
        interfaceInstances.forEach {
            webActivity.webView.addJavascriptInterface(it, "android_${it.javaClass.simpleName}")
        }
    }
}