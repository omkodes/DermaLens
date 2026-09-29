package com.example.dermalens.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = Screen.Home.route,
        title = "Home",
        icon = Icons.Default.Home
    )

    object Scan : BottomNavItem(
        route = Screen.Scan.route,
        title = "Scan",
        icon = Icons.Default.Search
    )

    object History : BottomNavItem(
        route = Screen.History.route,
        title = "History",
        icon = Icons.Default.List
    )

    companion object {
        val items = listOf(Home, Scan, History)
    }
}
