package de.honoka.lavender.lavsource.android.jsinterface.definition

import android.content.Intent
import android.webkit.JavascriptInterface
import de.honoka.lavender.lavsource.android.ui.WebActivity
import de.honoka.lavender.lavsource.android.util.ServerVariables

class BasicJsInterface(private val webActivity: WebActivity) {

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