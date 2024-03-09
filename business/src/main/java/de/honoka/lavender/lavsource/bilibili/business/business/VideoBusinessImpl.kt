package de.honoka.lavender.lavsource.bilibili.business.business

import cn.hutool.http.HttpResponse
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONArray
import cn.hutool.json.JSONNull
import cn.hutool.json.JSONObject
import de.honoka.lavender.api.business.VideoBusiness
import de.honoka.lavender.api.data.*
import de.honoka.lavender.api.util.LavsourceUtils
import de.honoka.lavender.api.util.toDurationString
import de.honoka.lavender.api.util.toStringWithUnit
import de.honoka.lavender.lavsource.bilibili.business.util.BilibiliUtils
import de.honoka.lavender.lavsource.bilibili.business.util.BilibiliUtils.executeWithBiliCookies
import de.honoka.sdk.util.kotlin.text.XmlUtils
import java.text.SimpleDateFormat
import java.util.*

object VideoBusinessImpl : VideoBusiness {

    override fun getRecommendedVideoList(): List<RecommendedVideoItem> {
        val url = if(BilibiliUtils.isLogined()) {
            "https://api.bilibili.com/x/web-interface/index/top/rcmd"
        } else {
            "https://api.bilibili.com/x/web-interface/wbi/index/top/feed/rcmd"
        }
        val json = BilibiliUtils.requestForJsonObject(url)
        val result = ArrayList<RecommendedVideoItem>()
        json.getByPath("data.item", JSONArray::class.java).forEach {
            //可以通过as，直接对it的类型进行断言，在此语句后使用it，都不用再进行类型转换
            it as JSONObject
            result.add(RecommendedVideoItem().apply {
                coverImg = LavsourceUtils.getProxiedImageUrl(it.getStr("pic"))
                playCount = it.getByPath("stat.view", Int::class.java).toStringWithUnit()
                danmakuCount = it.getByPath("stat.danmaku", Int::class.java).toStringWithUnit()
                duration = it.getInt("duration").toDurationString()
                author = it.getByPath("owner.name") as String
                title = it.getStr("title")
                videoId = it.getStr("bvid")
            })
        }
        return result
    }

    override fun getVideoDetails(id: String): VideoDetails {
        val url = "https://api.bilibili.com/x/web-interface/view/detail?bvid=$id"
        val json = BilibiliUtils.requestForJsonObject(url)
        val result = VideoDetails().apply {
            this.id = id
            uploader.run {
                this.id = json.getByPath("data.View.owner.mid", Long::class.java).toString()
                val userCardUrl = "https://api.bilibili.com/x/web-interface/card?mid=${this.id}"
                val userCardJson = BilibiliUtils.requestForJsonObject(userCardUrl)
                name = json.getByPath("data.View.owner.name") as String
                avatar = json.getByPath("data.View.owner.face", String::class.java).run {
                    LavsourceUtils.getProxiedImageUrl(this)
                }
                followerCount = json.getByPath("data.Card.card.fans", Int::class.java).toStringWithUnit()
                publishedVideosCount = userCardJson.getByPath(
                    "data.archive_count", Int::class.java
                ).toStringWithUnit()
            }
            title = json.getByPath("data.View.title") as String
            description = json.getByPath("data.View.desc") as String
            tagList = ArrayList<String>().apply {
                json.getByPath("data.Tags", JSONArray::class.java).forEach {
                    val jo = it as JSONObject
                    add(jo.getStr("tag_name"))
                }
            }
            playCount = json.getByPath("data.View.stat.view", Int::class.java).toStringWithUnit()
            danmakuCount = json.getByPath("data.View.stat.danmaku", Int::class.java).toStringWithUnit()
            publishTime = json.getByPath("data.View.pubdate", Long::class.java).run {
                val dateFormat = SimpleDateFormat("yyyy年M月d日 HH:mm", Locale.SIMPLIFIED_CHINESE)
                dateFormat.format(Date(this * 1000))
            }
            replyCount = json.getByPath("data.View.stat.reply", Int::class.java).toStringWithUnit()
            likeCount = json.getByPath("data.View.stat.like", Int::class.java).toStringWithUnit()
            coinCount = json.getByPath("data.View.stat.coin", Int::class.java).toStringWithUnit()
            collectCount = json.getByPath("data.View.stat.favorite", Int::class.java).toStringWithUnit()
            shareCount = json.getByPath("data.View.stat.share", Int::class.java).toStringWithUnit()
            relatedVideoList = ArrayList<RecommendedVideoItem>().apply {
                json.getByPath("data.Related", JSONArray::class.java).forEach {
                    it as JSONObject
                    add(RecommendedVideoItem().apply {
                        videoId = it.getStr("bvid")
                        coverImg = LavsourceUtils.getProxiedImageUrl(it.getStr("pic"))
                        duration = it.getInt("duration").toDurationString()
                        title = it.getStr("title")
                        author = it.getByPath("owner.name") as String
                        playCount = it.getByPath("stat.view", Int::class.java).toStringWithUnit()
                        danmakuCount = it.getByPath("stat.danmaku", Int::class.java).toStringWithUnit()
                    })
                }
            }
        }
        return result
    }

    override fun getCommentList(videoId: String, sortBy: String, page: Int): CommentList {
        val avId = BilibiliUtils.bvIdToAvId(videoId)
        val sortUrlParam: Int = when(sortBy.lowercase()) {
            "send_time" -> 0
            "like_count" -> 1
            "reply_count" -> 2
            else -> 1
        }
        val url = "https://api.bilibili.com/x/v2/reply?oid=$avId&type=1&sort=$sortUrlParam&pn=$page"
        val json = BilibiliUtils.requestForJsonObject(url)
        val result = CommentList().apply {
            if(page <= 1) {
                val top = json.getByPath("data.upper.top")
                if(top !is JSONNull) {
                    this.top = BilibiliBusiness.parseComment(top as JSONObject)
                }
            }
            val list = ArrayList<Comment>().also { this.list = it }
            json.getByPath("data.replies", JSONArray::class.java).forEach {
                list.add(BilibiliBusiness.parseComment(it as JSONObject))
            }
        }
        return result
    }

    override fun getCommentReplyList(videoId: String, commentId: String, page: Int): CommentList {
        val avId = BilibiliUtils.bvIdToAvId(videoId)
        val url = "https://api.bilibili.com/x/v2/reply/reply?oid=$avId&type=1&root=$commentId&pn=$page"
        val json = BilibiliUtils.requestForJsonObject(url)
        val result = CommentList().apply {
            val list = ArrayList<Comment>().also { this.list = it }
            json.getByPath("data.replies", JSONArray::class.java).forEach {
                list.add(BilibiliBusiness.parseComment(it as JSONObject))
            }
        }
        return result
    }

    override fun getEpisodeList(videoId: String): List<VideoEpisodeInfo> {
        val episodeUrl = "https://api.bilibili.com/x/player/pagelist?bvid=$videoId"
        val json = BilibiliUtils.requestForJsonObject(episodeUrl)
        val episodeList = ArrayList<VideoEpisodeInfo>()
        json.getJSONArray("data").forEach {
            it as JSONObject
            episodeList.add(VideoEpisodeInfo().apply {
                id = it.getLong("cid").toString()
                name = it.getStr("part")
            })
        }
        return episodeList
    }

    override fun getStreamUrlList(videoId: String, episodeId: String): List<VideoStreamInfo> {
        val urlToGetStreamUrl = "https://api.bilibili.com/x/player/wbi/playurl?bvid=$videoId&cid=$episodeId&fnval=16"
        val json = BilibiliUtils.requestForJsonObject(urlToGetStreamUrl)
        val qualityIdNameMap = HashMap<String, String>().apply {
            json.getByPath("data.support_formats", JSONArray::class.java).forEach {
                it as JSONObject
                this[it.getInt("quality").toString()] = it.getStr("new_description")
            }
        }
        val result = ArrayList<VideoStreamInfo>()
        val videoIndeoList = json.getByPath("data.dash.video", JSONArray::class.java).toList(JSONObject::class.java)
        val audioInfoList = json.getByPath("data.dash.audio", JSONArray::class.java).toList(JSONObject::class.java)
        val addedQualityId = HashSet<String>()
        videoIndeoList.forEachIndexed { i, it ->
            val qualityId = it.getInt("id").toString()
            if(addedQualityId.contains(qualityId)) return@forEachIndexed
            result.add(VideoStreamInfo().apply {
                type = "dash"
                this.qualityId = qualityId.toInt().toString()
                qualityName = qualityIdNameMap[qualityId]
                videoStreamUrl = LavsourceUtils.getProxiedMediaStreamUrl(it.getStr("base_url"))
                var audioIndex = i - videoIndeoList.size + audioInfoList.size
                if(audioIndex < 0) audioIndex = 0
                audioStreamUrl = audioInfoList[audioIndex].getStr("base_url").run {
                    LavsourceUtils.getProxiedMediaStreamUrl(this)
                }
            })
            addedQualityId.add(qualityId)
        }
        return result
    }

    override fun getVideoStreamResponse(url: String, range: String?): HttpResponse = HttpUtil.createGet(url).run {
        //header(HttpHeaders.Referrer, "https://www.bilibili.com/")
        //header(HttpHeaders.Range, range ?: "bytes=0-")
        //addBiliCookies()
        executeAsync()
    }

    override fun getDanmakuList(episodeId: String): List<DanmakuInfo> {
        val url = "https://api.bilibili.com/x/v1/dm/list.so?oid=$episodeId"
        val xml = XmlUtils.read(HttpUtil.createGet(url).executeWithBiliCookies().body())
        val result = ArrayList<DanmakuInfo>()
        xml.rootElement.elements("d").forEach {
            val danmakuAttrs = it.attribute("p").value.split(",")
            result.add(DanmakuInfo().apply {
                content = it.text
                time = danmakuAttrs[0].toDouble()
                type = BilibiliUtils.danmakuTypeToString(danmakuAttrs[1].toInt())
                fontSize = danmakuAttrs[2].toInt()
                colorRgb = "#${String.format("%06x", danmakuAttrs[3].toInt())}"
                senderId = danmakuAttrs[6]
            })
        }
        result.sortWith { o1, o2 -> o1.time!!.compareTo(o2.time!!) }
        return result
    }
}