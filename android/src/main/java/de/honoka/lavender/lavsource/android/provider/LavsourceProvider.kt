package de.honoka.lavender.lavsource.android.provider

import cn.hutool.json.JSON
import cn.hutool.json.JSONObject
import de.honoka.lavender.api.android.LavsourceProviderRequest
import de.honoka.lavender.lavsource.android.business.BasicBusiness
import de.honoka.lavender.lavsource.android.business.VideoBusiness
import de.honoka.sdk.util.android.common.BaseContentProvider
import de.honoka.sdk.util.android.common.toMethodArgs
import java.lang.reflect.Method

class LavsourceProvider : BaseContentProvider() {

    companion object {

        private val businessList: List<Any> = listOf(
            BasicBusiness(),
            VideoBusiness()
        )

        private val businessMap = HashMap<String, Any>().also { map ->
            businessList.forEach {
                map[it.javaClass.simpleName] = it
            }
        }
    }

    override fun call(method: String?, args: JSON?): Any? {
        args as JSONObject
        val request = args.toBean(LavsourceProviderRequest::class.java)
        val business = businessMap[request.className].also {
            it ?: throw Exception("Unknown class name: ${request.className}")
        }
        val methodObj: Method = business!!.javaClass.declaredMethods.run {
            forEach {
                if(it.name == request.method) return@run it
            }
            throw Exception("Unknown method name \"${request.method}\" of class: ${request.className}")
        }
        return methodObj.invoke(business, *request.args.toMethodArgs(methodObj))
    }
}