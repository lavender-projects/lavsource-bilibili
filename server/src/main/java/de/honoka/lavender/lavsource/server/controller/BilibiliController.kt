package de.honoka.lavender.lavsource.server.controller

import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import de.honoka.lavender.lavsource.bilibili.business.business.BilibiliBusiness
import de.honoka.lavender.lavsource.bilibili.business.data.BilibiliLoginParams
import de.honoka.lavender.lavsource.bilibili.business.util.BilibiliUtils.executeWithBiliCookies
import de.honoka.sdk.util.framework.web.ApiResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RequestMapping("/platform/bilibili")
@RestController
class BilibiliController {

    @GetMapping("/validateCode")
    fun validateCode(): ApiResponse<JSONObject> = ApiResponse.success(BilibiliBusiness.getValidateCode())

    @PostMapping("/login")
    fun login(@RequestBody params: BilibiliLoginParams): ApiResponse<*> {
        BilibiliBusiness.login(params)
        return ApiResponse.success()
    }

    @GetMapping("/loginStatus")
    fun loginStatus(): ApiResponse<JSONObject> = ApiResponse.success(BilibiliBusiness.getLoginStatus())

    @GetMapping("/image/proxy")
    fun imageProxy(@RequestParam url: String, response: HttpServletResponse) = response.run {
        val originalRes = HttpUtil.createGet(url).executeWithBiliCookies()
        outputStream.use {
            contentType = originalRes.header(HttpHeaders.CONTENT_TYPE)
            it.write(originalRes.bodyBytes())
        }
    }

    @GetMapping("/logout")
    fun logout(): ApiResponse<*> {
        BilibiliBusiness.logout()
        return ApiResponse.success()
    }
}