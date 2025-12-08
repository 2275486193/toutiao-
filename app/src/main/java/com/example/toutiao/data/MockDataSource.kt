package com.example.toutiao.data

import com.example.toutiao.data.model.NewsEntity
import com.example.toutiao.ui.component.FeedType
import com.google.gson.Gson
import java.util.*

object MockDataSource {
    private val titles = listOf(
        "习近平主席重要论述引发热议",
        "这档社交观察类综艺火了，全网笑出鹅叫声",
        "我国已有近3200公里高铁通车，世界领先",
        "北京今日多云转晴，气温适中",
        "AI 技术再突破，国产大模型发布",
        "世界杯预选赛：国足备战新阶段"
    )
    private val sources = listOf("海外网", "人民日报", "新华社", "央视新闻", "科技日报")
    private val images = listOf(
        "https://picsum.photos/200/150?random=1",
        "https://picsum.photos/200/150?random=2",
        "https://picsum.photos/200/150?random=3"
    )

    fun generate(size: Int = 20): List<NewsEntity> = (0 until size).map { idx ->
        val type = FeedType.entries.random()
        val imgCount = when (type) {
            FeedType.TEXT -> 0
            FeedType.RIGHT_IMG -> 1
            FeedType.VIDEO -> 1
            FeedType.THREE_IMG -> 3
        }
        NewsEntity(
            id = UUID.randomUUID().toString(),
            title = titles.random(),
            source = sources.random(),
            commentCount = (0..999).random(),
            publishTime = System.currentTimeMillis() - (0..86400000).random(),
            type = type.ordinal,
            imageUrls = Gson().toJson(images.shuffled().take(imgCount)),
            videoDuration = if (type == FeedType.VIDEO) "02:${(10..59).random()}" else null,
            isTop = idx < 3
        )
    }
}
