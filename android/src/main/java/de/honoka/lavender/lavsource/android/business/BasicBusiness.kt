package de.honoka.lavender.lavsource.android.business

import cn.hutool.json.JSONObject

class BasicBusiness {

    fun statusCheck(): JSONObject = JSONObject().set("status", true)
}