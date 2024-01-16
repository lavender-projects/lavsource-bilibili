package de.honoka.lavender.datasource.bilibili

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File

@SpringBootApplication
class BilibiliDataSourceApplication

fun main(args: Array<String>) {
    File("files").run { if(!exists()) mkdirs() }
    runApplication<BilibiliDataSourceApplication>(*args)
}