package com.example.toutiao.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_table")
data class NewsEntity(
    @PrimaryKey val id: String,
    val title: String,
    val source: String,
    val commentCount: Int,
    val publishTime: Long,
    val type: Int, // 0: TEXT, 1: RIGHT_IMG, 2: VIDEO, 3: THREE_IMG
    val imageUrls: String, // JSON list
    val videoDuration: String? = null,
    val isTop: Boolean = false
)