package com.seanof.sakugatomo.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector) {

    companion object {
        val items = listOf(
            NavigationItem(
                title = "Latest Posts",
                route = Screen.Latest.route,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            ),
            NavigationItem(
                title = "Liked Posts",
                route = Screen.Liked.route,
                selectedIcon = Icons.Filled.ThumbUp,
                unselectedIcon = Icons.Outlined.ThumbUp
            ),
            NavigationItem(
                title = "Popular Posts",
                route = Screen.Popular.route,
                selectedIcon = Icons.Filled.Star,
                unselectedIcon = Icons.Outlined.Star
            ),
            NavigationItem(
                title = "Search Posts",
                route = Screen.Search.route,
                selectedIcon = Icons.Filled.Search,
                unselectedIcon = Icons.Outlined.Search
            )
        )
    }
}
