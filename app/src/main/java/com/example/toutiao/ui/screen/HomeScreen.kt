package com.example.toutiao.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: HomeViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(selected = selected, onSelect = { selected = it }) }
    ) { padding ->
        when (selected) {
            0 -> {
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
    onLoadMore: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(list) { idx, item ->
            FeedCard(item)
            if (idx >= list.size - 3) {
                LaunchedEffect(list.size) { onLoadMore() }
            }
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
