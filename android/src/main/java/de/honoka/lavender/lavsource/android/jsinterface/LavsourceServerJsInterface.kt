package de.honoka.lavender.lavsource.android.jsinterface

import android.webkit.JavascriptInterface
import de.honoka.lavender.lavsource.android.util.LavsourceServerVariables

class LavsourceServerJsInterface {

    @JavascriptInterface
    fun getUrlPrefix() = "http://localhost:${LavsourceServerVariables.serverPort}"
}