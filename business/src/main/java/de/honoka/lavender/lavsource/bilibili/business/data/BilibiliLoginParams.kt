package de.honoka.lavender.lavsource.bilibili.business.data

import java.io.Serializable

data class BilibiliLoginParams(

    var username: String? = null,

    var password: String? = null,

    var keep: Int? = 0,

    var token: String? = null,

    var challenge: String? = null,

    var validate: String? = null,

    var seccode: String? = null
) : Serializable