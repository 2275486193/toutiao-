package com.example.toutiao.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toutiao.ui.theme.ToutiaoRed

@Composable
fun TabBar(
    tabs: List<String> = listOf("关注", "推荐", "热榜", "新时代", "小说", "视频"),
    selected: Int = 1,
    onSelect: (Int) -> Unit = {}
) {
    Surface(color = Color.White, shadowElevation = 2.dp) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(tabs) { index, title ->
                val isSelected = index == selected
                TextButton(
                    onClick = { onSelect(index) },
                    modifier = Modifier.padding(horizontal = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = title,
                            color = if (isSelected) ToutiaoRed else Color.Gray,
                            fontSize = if (isSelected) 16.sp else 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(3.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(ToutiaoRed)
                            )
                        }
                    }
                }
            }
        }
    }
}
