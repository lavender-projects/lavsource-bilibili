package de.honoka.lavender.lavsource.bilibili

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File

@SpringBootApplication
class BilibiliLavsourceApplication

fun main(args: Array<String>) {
    File("files").run { if(!exists()) mkdirs() }
    runApplication<BilibiliLavsourceApplication>(*args)
}