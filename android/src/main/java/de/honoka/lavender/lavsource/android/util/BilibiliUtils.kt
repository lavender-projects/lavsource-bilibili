package de.honoka.lavender.lavsource.android.util

import de.honoka.lavender.lavsource.bilibili.business.util.BilibiliUtils
import de.honoka.sdk.util.android.common.GlobalComponents
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.Path

object BilibiliUtilsAbstractPartImpl : BilibiliUtils.AbstractPart {

    override val cookiesFilePath: Path = Path(
        GlobalComponents.application.dataDir.absolutePath,
        "lavender/bilibili/data/cookies.json"
    )

    override fun openStaticRequestHeadersFileStream(): InputStream = run {
        GlobalComponents.application.assets.open("lavsource/bilibili/staticRequestHeaders.json")
    }
}