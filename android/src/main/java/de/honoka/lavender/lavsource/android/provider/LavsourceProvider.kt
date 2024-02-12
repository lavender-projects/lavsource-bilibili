package de.honoka.lavender.lavsource.android.provider

import cn.hutool.json.JSON
import cn.hutool.json.JSONObject
import de.honoka.lavender.lavsource.android.util.LavsourceServer
import de.honoka.sdk.util.android.common.BaseContentProvider

class LavsourceProvider : BaseContentProvider() {

    override fun call(args: JSON?): JSONObject = JSONObject().also {
        it["status"] = LavsourceServer.isServerRunning().also innerAlso@ { status ->
            if(status) return@innerAlso
            LavsourceServer.checkOrRestartInstance()
        }
    }
}