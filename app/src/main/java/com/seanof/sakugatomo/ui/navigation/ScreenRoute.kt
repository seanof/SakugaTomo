package com.seanof.sakugatomo.ui.navigation

import kotlinx.serialization.Serializable

sealed class ScreenRoute(val route: String) {
    data object Latest: ScreenRoute("latest_screen")
    data object Favourites: ScreenRoute("favourites_screen")
    data object Popular: ScreenRoute("popular_screen")
    data object Search: ScreenRoute("search_screen")
    @Serializable
    data class Player(val uri: String)
}
