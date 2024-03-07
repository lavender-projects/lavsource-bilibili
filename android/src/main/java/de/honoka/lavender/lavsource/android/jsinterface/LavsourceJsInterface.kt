package de.honoka.lavender.lavsource.android.jsinterface

import android.webkit.JavascriptInterface
import de.honoka.sdk.util.android.server.HttpServerVariables

class LavsourceJsInterface {

    @JavascriptInterface
    fun getUrlPrefix() = HttpServerVariables.getApiUrlByPath("")
}