package de.honoka.lavender.lavsource.server.util

import de.honoka.lavender.lavsource.bilibili.business.util.BilibiliUtils
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.Path

object BilibiliUtilsAbstractPartImpl : BilibiliUtils.AbstractPart {

    override val cookiesFilePath: Path = Path(EnvironmentPathUtils.dataDirPathOfApp, "data/cookies.json")

    override fun openStaticRequestHeadersFileStream(): InputStream = run {
        javaClass.getResource("/bilibili/staticRequestHeaders.json")!!.openStream()
    }
}