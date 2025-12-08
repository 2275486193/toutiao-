package com.example.toutiao.ui.state

sealed interface HomeIntent {
    object LoadInitial : HomeIntent
    object Refresh : HomeIntent
    object LoadMore : HomeIntent
}