package de.honoka.lavender.lavsource.android.provider

import cn.hutool.json.JSON
import de.honoka.lavender.lavsource.android.util.LavsourceServer
import de.honoka.sdk.util.android.common.BaseContentProvider

class LavsourceProvider : BaseContentProvider() {

    override fun call(args: JSON?): Boolean = LavsourceServer.isServerRunning().also {
        if(it) return@also
        LavsourceServer.checkOrRestartInstance()
    }
}