package de.honoka.lavender.lavsource.android.jsinterface

import cn.hutool.json.JSONObject
import de.honoka.lavender.lavsource.bilibili.business.business.BilibiliBusiness
import de.honoka.lavender.lavsource.bilibili.business.data.BilibiliLoginParams
import de.honoka.sdk.util.android.jsinterface.async.AsyncJavascriptInterface

class BilibiliJsInterface {

    @AsyncJavascriptInterface
    fun validateCode(): JSONObject = BilibiliBusiness.getValidateCode()

    @AsyncJavascriptInterface
    fun login(params: BilibiliLoginParams) = BilibiliBusiness.login(params)

    @AsyncJavascriptInterface
    fun loginStatus(): JSONObject = BilibiliBusiness.getLoginStatus()

    @AsyncJavascriptInterface
    fun logout() = BilibiliBusiness.logout()
}