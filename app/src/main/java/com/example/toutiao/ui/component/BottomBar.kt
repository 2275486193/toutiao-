package com.example.toutiao.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import com.example.toutiao.ui.theme.ToutiaoRed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person

@Composable
fun BottomBar(
    selected: Int = 0,
    onSelect: (Int) -> Unit = {}
) {
    val items = listOf("首页", "视频", "搜索", "任务", "我的")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.PlayArrow,
        Icons.Default.Search,
        Icons.Default.CheckCircle,
        Icons.Default.Person
    )
    NavigationBar(containerColor = Color.White, tonalElevation = 2.dp) {
        items.forEachIndexed { index, label ->
            NavigationBarItem(
                selected = index == selected,
                onClick = { onSelect(index) },
                icon = {
                    Icon(
                        imageVector = icons[index],
                        contentDescription = label,
                        tint = if (index == selected) ToutiaoRed else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = if (index == selected) ToutiaoRed else Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = if (index == selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}
