package de.honoka.lavender.datasource.bilibili

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BilibiliDataSourceApplication

fun main(args: Array<String>) {
    runApplication<BilibiliDataSourceApplication>(*args)
}