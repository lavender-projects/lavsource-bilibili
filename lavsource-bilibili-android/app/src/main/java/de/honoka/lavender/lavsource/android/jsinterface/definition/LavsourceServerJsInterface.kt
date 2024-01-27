package de.honoka.lavender.lavsource.android.jsinterface.definition

import android.webkit.JavascriptInterface
import de.honoka.lavender.lavsource.android.util.ServerVariables

class LavsourceServerJsInterface {

    @JavascriptInterface
    fun getUrlPrefix() = "http://localhost:${ServerVariables.lavsourceServerPort}"
}