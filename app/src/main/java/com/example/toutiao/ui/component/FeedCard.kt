package com.example.toutiao.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.toutiao.ui.theme.SecondaryText
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.painter.ColorPainter
import coil.request.ImageRequest

enum class FeedType { TEXT, RIGHT_IMG, VIDEO, THREE_IMG }

data class FeedItem(
    val id: String,
    val type: FeedType,
    val title: String,
    val source: String,
    val commentCount: Int,
    val publishTime: String,
    val imageUrls: List<String> = emptyList(),
    val videoDuration: String? = null,
    val isTop: Boolean = false
)

@Composable
fun FeedCard(item: FeedItem, index: Int? = null, onClose: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        when (item.type) {
            FeedType.TEXT -> TextCard(item, index)
            FeedType.RIGHT_IMG -> RightImgCard(item, index)
            FeedType.VIDEO -> VideoCard(item, index)
            FeedType.THREE_IMG -> ThreeImgCard(item, index)
        }
    }
}

@Composable
private fun TextCard(item: FeedItem, index: Int?) {
    Column(Modifier.padding(12.dp)) {
        Title(item, index)
        Spacer(Modifier.height(6.dp))
        Meta(item)
    }
}

@Composable
private fun RightImgCard(item: FeedItem, index: Int?) {
    Row(Modifier.padding(12.dp)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Title(item, index)
            Spacer(Modifier.height(6.dp))
            Meta(item)
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.imageUrls.firstOrNull())
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(96.dp, 64.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Color.LightGray)
        )
    }
}

@Composable
private fun VideoCard(item: FeedItem, index: Int?) {
    Column(Modifier.padding(12.dp)) {
        Title(item, index)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrls.firstOrNull())
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(Color.LightGray)
            )
            // 播放图标
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
            // 时长
            item.videoDuration?.let {
                Text(
                    text = it,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Meta(item)
    }
}

@Composable
private fun ThreeImgCard(item: FeedItem, index: Int?) {
    Column(Modifier.padding(12.dp)) {
        Title(item, index)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            item.imageUrls.take(3).forEach { url ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(Color.LightGray)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Meta(item)
    }
}

@Composable
private fun Title(item: FeedItem, index: Int?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (index != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Gray.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "#${index}",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = item.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Meta(item: FeedItem) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (item.isTop) {
            Text(
                text = "置顶",
                color = Color.Red,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(2.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = item.source,
            color = SecondaryText,
            fontSize = 12.sp
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "${item.commentCount}评论",
            color = SecondaryText,
            fontSize = 12.sp
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = item.publishTime,
            color = SecondaryText,
            fontSize = 12.sp
        )
        IconButton(
            onClick = {},
            modifier = Modifier.size(16.dp)
        ) {
            Text(
                text = "✕",
                color = SecondaryText,
                fontSize = 12.sp
            )
        }
    }
}
