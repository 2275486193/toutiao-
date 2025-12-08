package com.example.toutiao.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toutiao.ui.theme.ToutiaoRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    city: String = "北京",
    temperature: String = "14°",
    weather: String = "多云",
    onPublishClick: () -> Unit = {},
    onAiClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧天气
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = temperature,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = city,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = weather,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                // 右侧功能
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPublishClick) {
                        Icon(
                            imageVector = Icons.Default.Search, // 临时用搜索图标代替发布
                            contentDescription = "发布",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onAiClick) {
                        Text(
                            text = "AI",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ToutiaoRed,
            titleContentColor = Color.White
        )
    )
}