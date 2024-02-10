package de.honoka.lavender.lavsource.android.jsinterface

import android.content.Intent
import android.webkit.JavascriptInterface
import de.honoka.lavender.lavsource.android.ui.WebActivity
import de.honoka.sdk.util.android.server.HttpServerVariables

class BasicJsInterface(private val webActivity: WebActivity) {

    @JavascriptInterface
    fun openNewWebActivity(path: String) {
        webActivity.run {
            startActivity(Intent(this, WebActivity::class.java).apply {
                putExtra("url", HttpServerVariables.getUrlByPrefix(path))
            })
        }
    }

    @JavascriptInterface
    fun finishCurrentWebActivity() {
        webActivity.finish()
    }
}