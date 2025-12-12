package com.example.toutiao.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toutiao.data.repository.NewsRepository
import com.example.toutiao.ui.component.FeedItem
import com.example.toutiao.ui.component.FeedType
import com.example.toutiao.ui.state.HomeIntent
import com.example.toutiao.ui.state.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import javax.inject.Inject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: NewsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        dispatch(HomeIntent.LoadInitial)
    }

    fun dispatch(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadInitial -> loadInitial()
            HomeIntent.Refresh -> refresh()
            HomeIntent.LoadMore -> loadMore()
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repo.refresh()
            repo.newsFlow().collect { list ->
                if (_state.value.isRefreshing) return@collect
                _state.update {
                    it.copy(
                        isLoading = false,
                        feedList = list.map { en ->
                            val images: List<String> = try {
                                val type = object : TypeToken<List<String>>() {}.type
                                Gson().fromJson(en.imageUrls, type)
                            } catch (_: Exception) { emptyList() }
                            FeedItem(
                                id = en.id,
                                type = FeedType.entries[en.type],
                                title = en.title,
                                source = en.source,
                                commentCount = en.commentCount,
                                publishTime = "刚刚",
                                imageUrls = images,
                                videoDuration = en.videoDuration,
                                isTop = en.isTop
                            )
                        }
                    )
                }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            val hold = async { delay(2000) }
            repo.refresh()
            val latest = repo.newsFlow().first()
            hold.await()
            _state.update { it.copy(isRefreshing = false, feedList = latest.map { en ->
                val images: List<String> = try {
                    val type = object : TypeToken<List<String>>() {}.type
                    Gson().fromJson(en.imageUrls, type)
                } catch (_: Exception) { emptyList() }
                FeedItem(
                    id = en.id,
                    type = FeedType.entries[en.type],
                    title = en.title,
                    source = en.source,
                    commentCount = en.commentCount,
                    publishTime = "刚刚",
                    imageUrls = images,
                    videoDuration = en.videoDuration,
                    isTop = en.isTop
                )
            } ) }
        }
    }

    private fun loadMore() {
        if (_state.value.isLoadingMore) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            delay(2000)
            repo.loadMore()
            _state.update { it.copy(isLoadingMore = false) }
        }
    }
}
