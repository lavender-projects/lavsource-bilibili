package de.honoka.lavender.lavsource.android.provider

import cn.hutool.json.JSON
import cn.hutool.json.JSONObject
import de.honoka.lavender.lavsource.android.util.LavsourceServer
import de.honoka.lavender.lavsource.android.util.LavsourceServerUtils
import de.honoka.lavender.lavsource.android.util.LavsourceServerVariables
import de.honoka.sdk.util.android.common.BaseContentProvider

class LavsourceProvider : BaseContentProvider() {

    override fun call(method: String?, args: JSON?): Any? = when(method) {
        null -> statusCheck()
        "getBaseUrl" -> LavsourceServerVariables.getUrlByPrefix("")
        else -> null
    }

    private fun statusCheck() = JSONObject().also {
        it["status"] = LavsourceServer.isServerRunning().also innerAlso@ { status ->
            if(status && LavsourceServerUtils.allEnvironmentsInitialized) return@innerAlso
            LavsourceServerUtils.initTermuxEnvironment()
            LavsourceServerUtils.initLavsourceServer()
            LavsourceServerUtils.allEnvironmentsInitialized = true
        }
    }
}