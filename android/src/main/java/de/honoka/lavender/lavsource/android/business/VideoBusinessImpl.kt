package de.honoka.lavender.lavsource.android.business

import cn.hutool.http.HttpResponse
import de.honoka.lavender.api.business.VideoBusiness
import de.honoka.lavender.api.data.*

class VideoBusinessImpl : VideoBusiness {

    override fun getCommentList(videoId: String, sortBy: String, page: Int): CommentList {
        TODO("Not yet implemented")
    }

    override fun getCommentReplyList(videoId: String, commentId: Long, page: Int): CommentList {
        TODO("Not yet implemented")
    }

    override fun getDanmakuList(episodeId: Long): List<DanmakuInfo> {
        TODO("Not yet implemented")
    }

    override fun getEpisodeList(videoId: String): List<VideoEpisodeInfo> {
        TODO("Not yet implemented")
    }

    override fun getRecommendedVideoList(): List<RecommendedVideoItem> {
        TODO("Not yet implemented")
    }

    override fun getStreamUrlList(videoId: String, episodeId: Long): List<VideoStreamInfo> {
        TODO("Not yet implemented")
    }

    override fun getVideoDetails(id: String): VideoDetails {
        TODO("Not yet implemented")
    }

    override fun getVideoStreamResponse(url: String, range: String?): HttpResponse {
        TODO("Not yet implemented")
    }
}