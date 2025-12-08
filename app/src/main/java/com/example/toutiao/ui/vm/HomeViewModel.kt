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
import javax.inject.Inject

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
                _state.update {
                    it.copy(
                        isLoading = false,
                        feedList = list.map { en ->
                            FeedItem(
                                id = en.id,
                                type = FeedType.entries[en.type],
                                title = en.title,
                                source = en.source,
                                commentCount = en.commentCount,
                                publishTime = "刚刚",
                                imageUrls = listOf("https://picsum.photos/200/150?random=${en.id.take(8)}"),
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
            repo.refresh()
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private fun loadMore() {
        viewModelScope.launch {
            repo.loadMore()
        }
    }
}
