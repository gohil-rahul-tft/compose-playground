package com.example.composeplayground.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicVideo
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.composeplayground.screens.MovieScreen
import com.example.composeplayground.screens.MusicScreen

typealias ComposableFun = @Composable () -> Unit

sealed class TabItem(var icon: ImageVector, var title: String, var screen: ComposableFun) {
    object Music : TabItem(Icons.Default.MusicVideo, "Music", { MusicScreen() })
    object Movies : TabItem(Icons.Default.Movie, "Movies", { MovieScreen() })
}