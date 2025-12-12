package com.example.toutiao.ui.state

import com.example.toutiao.ui.component.FeedItem

data class HomeState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val feedList: List<FeedItem> = emptyList(),
    val error: String? = null
)
