package com.example.toutiao.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.toutiao.ui.component.*
import com.example.toutiao.ui.state.HomeIntent
import com.example.toutiao.ui.vm.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: HomeViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar() }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            SearchBar()
            TabBar()
            Box(Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    state.feedList.isEmpty() -> Text(
                        "暂无数据",
                        Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    else -> FeedList(
                        list = state.feedList,
                        onLoadMore = { vm.dispatch(HomeIntent.LoadMore) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedList(
    list: List<FeedItem>,
    onLoadMore: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(list) { idx, item ->
            FeedCard(item)
            if (idx >= list.size - 3) {
                LaunchedEffect(Unit) { onLoadMore() }
            }
        }
    }
}
