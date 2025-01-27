package com.seanof.sakugatomo.ui

sealed class Screen(val route: String) {
    data object Latest: Screen("latest_screen")
    data object Liked: Screen("liked_screen")
    data object Popular: Screen("popular_screen")
    data object Search: Screen("search_screen")
}