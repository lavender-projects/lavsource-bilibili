package de.honoka.lavender.lavsource.server.service

import de.honoka.lavender.lavsource.bilibili.business.util.BilibiliUtils
import de.honoka.lavender.lavsource.server.util.BilibiliUtilsAbstractPartImpl
import de.honoka.lavender.lavsource.starter.service.SystemInitService
import org.springframework.stereotype.Service

@Service
class SystemInitServiceImpl : SystemInitService() {

    override fun init() {
        BilibiliUtils.initAbstractPart(BilibiliUtilsAbstractPartImpl)
    }
}