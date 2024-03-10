package de.honoka.lavender.lavsource.bilibili.service

import de.honoka.lavender.api.business.VideoBusiness
import de.honoka.lavender.lavsource.bilibili.business.business.VideoBusinessImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BusinessImplConfig {

    @Bean
    fun videoBusiness(): VideoBusiness = VideoBusinessImpl
}