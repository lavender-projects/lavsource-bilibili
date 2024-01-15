package de.honoka.lavender.datasource.bilibili.controller

import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import de.honoka.lavender.datasource.bilibili.data.BilibiliLoginParams
import de.honoka.lavender.datasource.bilibili.service.BilibiliService
import de.honoka.lavender.datasource.bilibili.util.BilibiliUtils.executeWithBiliCookies
import de.honoka.sdk.util.framework.web.ApiResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RequestMapping("/platform/bilibili")
@RestController
class BilibiliController(
    private val bilibiliService: BilibiliService
) {

    @GetMapping("/validateCode")
    fun validateCode(): ApiResponse<JSONObject> = ApiResponse.success(bilibiliService.getValidateCode())

    @PostMapping("/login")
    fun login(@RequestBody params: BilibiliLoginParams): ApiResponse<Unit> {
        bilibiliService.login(params)
        return ApiResponse.success(null)
    }

    @GetMapping("/loginStatus")
    fun loginStatus(): ApiResponse<JSONObject> = ApiResponse.success(bilibiliService.getLoginStatus())

    @GetMapping("/image/proxy")
    fun imageProxy(@RequestParam url: String, response: HttpServletResponse) = response.run {
        val originalRes = HttpUtil.createGet(url).executeWithBiliCookies()
        outputStream.use {
            contentType = originalRes.header(HttpHeaders.CONTENT_TYPE)
            it.write(originalRes.bodyBytes())
        }
    }

    @GetMapping("/logout")
    fun logout(): ApiResponse<Unit> {
        bilibiliService.logout()
        return ApiResponse.success(null)
    }
}