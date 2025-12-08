package com.example.toutiao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.toutiao.ui.screen.HomeScreen
import com.example.toutiao.ui.theme.ToutiaoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToutiaoTheme {
                HomeScreen()
            }
        }
    }
}

// Preview 仅供 IDE 使用，避免将预览 Activity 合并进运行时 Manifest
