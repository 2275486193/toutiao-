package com.example.toutiao.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.toutiao.ui.component.*
import com.example.toutiao.ui.state.HomeIntent
import com.example.toutiao.ui.vm.HomeViewModel
import com.example.toutiao.ui.theme.ToutiaoRed
import com.example.toutiao.ui.theme.SecondaryText
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: HomeViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf(0) }
    val feedListState = rememberLazyListState()

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(selected = selected, onSelect = { selected = it }) }
    ) { padding ->
        when (selected) {
            0 -> {
                Column(Modifier.padding(padding)) {
                    SearchBar()
                    TabBar()
                    val refreshState = rememberPullToRefreshState()
                    val density = LocalDensity.current
                    val contentOffsetPx = with(density) {
                        PullToRefreshDefaults.PositionalThreshold.toPx() * refreshState.distanceFraction
                    }
                    PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = { vm.dispatch(HomeIntent.Refresh) },
                        state = refreshState
                    ) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .graphicsLayer { translationY = contentOffsetPx }
                        ) {
                            when {
                                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                                state.feedList.isEmpty() -> Text(
                                    "暂无数据",
                                    Modifier.align(Alignment.Center),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                else -> FeedList(
                                    list = state.feedList,
                                    isLoadingMore = state.isLoadingMore,
                                    onLoadMore = { vm.dispatch(HomeIntent.LoadMore) },
                                    listState = feedListState
                                )
                            }
                        }
                    }
                }
            }
            4 -> {
                MyScreen(padding)
            }
            else -> {
                Box(Modifier.padding(padding).fillMaxSize()) {
                    Text(
                        text = "开发中",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedList(
    list: List<FeedItem>,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    listState: LazyListState
) {
    Box {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            state = listState
        ) {
            itemsIndexed(list, key = { _, item -> item.id }) { index, item ->
                FeedCard(item, index + 1)
            }
            item {
                LoadMoreFooter(isLoading = isLoadingMore)
            }
        }
        VerticalLazyScrollbar(
            listState = listState,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(end = 2.dp)
        )
    }
    LaunchedEffect(listState, list.size) {
        var stableFrames = 0
        while (true) {
            val info = listState.layoutInfo
            val footerIndex = list.size
            val footerItem = info.visibleItemsInfo.find { it.index == footerIndex }
            val viewportEnd = info.viewportEndOffset
            val footerFullyVisible = footerItem != null && (footerItem.offset + footerItem.size) <= viewportEnd
            if (footerFullyVisible && listState.isScrollInProgress) {
                stableFrames++
            } else {
                stableFrames = 0
            }
            if (stableFrames >= 2 && !isLoadingMore && list.isNotEmpty()) {
                stableFrames = 0
                onLoadMore()
            }
            delay(120)
        }
    }
}

@Composable
private fun VerticalLazyScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val info = listState.layoutInfo
    val total = info.totalItemsCount
    if (total <= 0) return

    val visibleCount = info.visibleItemsInfo.size
    val firstIndex = info.visibleItemsInfo.firstOrNull()?.index ?: 0
    val targetFraction = firstIndex.toFloat() / kotlin.math.max(total - visibleCount, 1)
    val fraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(durationMillis = 120)
    )
    val thumbFraction = 0.12f
    var isVisible = listState.isScrollInProgress
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .width(6.dp)
            .drawBehind {
                if (alpha <= 0f) return@drawBehind
                drawRoundRect(
                    color = Color.LightGray.copy(alpha = 0.25f * alpha),
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                )
                val thumbHeight = size.height * thumbFraction
                val top = (size.height - thumbHeight) * fraction
                drawRoundRect(
                    color = ToutiaoRed.copy(alpha = 0.65f * alpha),
                    topLeft = Offset(0f, top),
                    size = Size(size.width, thumbHeight),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                )
            }
            .pointerInput(total, visibleCount) {
                detectTapGestures { pos ->
                    isVisible = true
                    val li = listState.layoutInfo
                    val vCount = li.visibleItemsInfo.size
                    val tCount = li.totalItemsCount
                    if (tCount <= 0 || vCount <= 0) return@detectTapGestures
                    val first = li.visibleItemsInfo.firstOrNull()?.index ?: 0
                    val frac = first.toFloat() / kotlin.math.max(tCount - vCount, 1)
                    val thumbFraction = kotlin.math.max(vCount.toFloat() / kotlin.math.max(tCount.toFloat(), 1f), 0.05f)
                    val thumbHeight = size.height * thumbFraction
                    val top = (size.height - thumbHeight) * frac
                    val insideThumb = pos.y in top..(top + thumbHeight)
                    if (!insideThumb) return@detectTapGestures
                    val yFrac = (pos.y / size.height).coerceIn(0f, 1f)
                    val target = ((tCount - vCount) * yFrac).toInt()
                    scope.launch { listState.scrollToItem(target) }
                }
            }
            .pointerInput(total, visibleCount) {
                var dragAllowed = false
                detectDragGestures(
                    onDragStart = { start ->
                        isVisible = true
                        val li = listState.layoutInfo
                        val vCount = li.visibleItemsInfo.size
                        val tCount = li.totalItemsCount
                        if (tCount <= 0 || vCount <= 0) { dragAllowed = false; return@detectDragGestures }
                        val first = li.visibleItemsInfo.firstOrNull()?.index ?: 0
                        val frac = first.toFloat() / kotlin.math.max(tCount - vCount, 1)
                        val thumbFraction = kotlin.math.max(vCount.toFloat() / kotlin.math.max(tCount.toFloat(), 1f), 0.05f)
                        val thumbHeight = size.height * thumbFraction
                        val top = (size.height - thumbHeight) * frac
                        dragAllowed = start.y in top..(top + thumbHeight)
                    },
                    onDragEnd = { dragAllowed = false; isVisible = false }
                ) { change, _ ->
                    if (!dragAllowed) return@detectDragGestures
                    val li = listState.layoutInfo
                    val vCount = li.visibleItemsInfo.size
                    val tCount = li.totalItemsCount
                    if (tCount <= 0 || vCount <= 0) return@detectDragGestures
                    val yFrac = (change.position.y / size.height).coerceIn(0f, 1f)
                    val target = ((tCount - vCount) * yFrac).toInt()
                    scope.launch { listState.scrollToItem(target) }
                    change.consume()
                }
            }
    )
}

@Composable
private fun LoadMoreFooter(isLoading: Boolean) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLoading) {
            CircularProgressIndicator(Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("正在加载更多…", style = MaterialTheme.typography.bodySmall)
        } else {
            Text("继续上拉加载更多", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
        }
    }
}

@Composable
private fun MyScreen(padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ToutiaoRed)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = "https://picsum.photos/100",
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(32.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "头条用户",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ID: 12345678",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "等级 Lv5",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("关注", "128")
                StatItem("粉丝", "2.6万")
                StatItem("获赞", "1.3万")
            }
        }
        item {
            Card(Modifier.padding(horizontal = 16.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickItem(Icons.Outlined.FavoriteBorder, "收藏")
                        QuickItem(Icons.Outlined.History, "历史")
                        QuickItem(Icons.Outlined.ShoppingCart, "订单")
                        QuickItem(Icons.Outlined.Settings, "设置")
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickItem(Icons.Outlined.ChatBubbleOutline, "消息")
                        QuickItem(Icons.Outlined.Assignment, "任务中心")
                        QuickItem(Icons.Outlined.Article, "创作中心")
                        QuickItem(Icons.Outlined.NotificationsNone, "通知")
                    }
                }
            }
        }
        item {
            Card(Modifier.padding(horizontal = 16.dp)) {
                Column {
                    ListItem(
                        leadingContent = { Icon(Icons.Outlined.ChatBubbleOutline, null) },
                        headlineContent = { Text("互动消息") },
                        trailingContent = { Icon(Icons.Outlined.ChevronRight, null) }
                    )
                    Divider()
                    ListItem(
                        leadingContent = { Icon(Icons.Outlined.NotificationsNone, null) },
                        headlineContent = { Text("通知设置") },
                        trailingContent = { Icon(Icons.Outlined.ChevronRight, null) }
                    )
                    Divider()
                    ListItem(
                        leadingContent = { Icon(Icons.Outlined.HelpOutline, null) },
                        headlineContent = { Text("帮助与反馈") },
                        trailingContent = { Icon(Icons.Outlined.ChevronRight, null) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(text = label, color = SecondaryText, fontSize = 12.sp)
    }
}

@Composable
private fun QuickItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Column(
        Modifier
            .width(72.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = ToutiaoRed)
        Spacer(Modifier.height(6.dp))
        Text(text = label, fontSize = 12.sp)
    }
}
