package de.honoka.lavender.lavsource.android.test.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.honoka.lavender.lavsource.bilibili.business.util.BilibiliUtils
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AllAndroidTest {

    @Test
    fun test1() {
        val key = """
            -----BEGIN PUBLIC KEY-----
            MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDjb4V7EidX/ym28t2ybo0U6t0n
            6p4ej8VjqKHg100va6jkNbNTrLQqMCQCAYtXMXXp2Fwkk6WR+12N9zknLjf+C9sx
            /+l48mjUU8RqahiFD1XT/u2e0m2EN029OhCgkHx3Fc/KlFSIbak93EH/XlYis0w+
            Xl69GV6klzgxW6d2xQIDAQAB
            -----END PUBLIC KEY-----
        """.trimIndent().trim()
        val hash = "00f2c43f56e65b1d"
        val res = BilibiliUtils.encryptPassword("12345abcde", key, hash)
        println(res)
    }
}