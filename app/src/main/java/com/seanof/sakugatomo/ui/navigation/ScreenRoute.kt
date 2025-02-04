package com.seanof.sakugatomo.ui.navigation

sealed class ScreenRoute(val route: String) {
    data object Latest: ScreenRoute("latest_screen")
    data object Liked: ScreenRoute("liked_screen")
    data object Popular: ScreenRoute("popular_screen")
    data object Search: ScreenRoute("search_screen")
}