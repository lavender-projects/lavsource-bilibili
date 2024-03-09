package de.honoka.lavender.lavsource.bilibili

import de.honoka.lavender.lavsource.bilibili.util.EmbeddedDatabaseUtils
import de.honoka.sdk.util.framework.database.AbstractEmbeddedDatabaseUtils
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BilibiliLavsourceApplication

fun main(args: Array<String>) {
    EmbeddedDatabaseUtils.setJdbcUrlRelatedWithDataDirInJvmProps(
        AbstractEmbeddedDatabaseUtils.Database.H2,
        "data/lavsource-bilibili-server"
    )
    runApplication<BilibiliLavsourceApplication>(*args)
}