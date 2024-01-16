package de.honoka.lavender.lavsource.android.util

import android.content.Intent
import android.webkit.JavascriptInterface
import de.honoka.lavender.lavsource.android.ui.WebActivity

object JavaScriptInterfaces {

    fun newAll(webActivity: WebActivity) = arrayOf(
        BasicJsInterface(webActivity),
        LavsourceServerJsInterface()
    )
}

class BasicJsInterface(
    private val webActivity: WebActivity
) {

    @JavascriptInterface
    fun openNewWebActivity(path: String) {
        webActivity.run {
            startActivity(Intent(this, WebActivity::class.java).apply {
                putExtra("url", ServerVariables.getUrlByWebServerPrefix(path))
            })
        }
    }

    @JavascriptInterface
    fun finishCurrentWebActivity() {
        webActivity.finish()
    }
}

class LavsourceServerJsInterface {

    @JavascriptInterface
    fun getUrlPrefix() = "http://localhost:${ServerVariables.lavsourceServerPort}"
}