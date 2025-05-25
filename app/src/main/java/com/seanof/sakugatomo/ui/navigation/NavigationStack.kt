package com.seanof.sakugatomo.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.seanof.sakugatomo.SakugaTomoViewModel
import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.data.model.SakugaTag
import com.seanof.sakugatomo.ui.SakugaItemsGrid
import com.seanof.sakugatomo.ui.SakugaPlayer

@Composable
fun NavigationStack(navHostController: NavHostController,
                    padding: PaddingValues,
                    uiState: SakugaTomoViewModel.ScreenUiState,
                    savedPosts: List<SakugaPost>,
                    likedPosts: (List<SakugaPost>?, List<SakugaPost>) -> Unit,
                    sakugaTagsList: List<SakugaTag>,
                    onSaveItemToDownloads: (Context, String, String) -> Unit,
                    onItemLiked: (SakugaPost) -> Unit = {},
                    onItemDelete: (SakugaPost) -> Unit = {}) {

    val routes = listOf(
        ScreenRoute.Latest,
        ScreenRoute.Favourites,
        ScreenRoute.Popular,
        ScreenRoute.Search
    )

    NavHost(navController = navHostController, startDestination = ScreenRoute.Latest.route) {
        routes.forEach { screenRoute ->
            composable(route = screenRoute.route) {
                SakugaItemsGrid(
                    padding = padding,
                    uiState = uiState,
                    likedPosts = savedPosts,
                    currentRoute = screenRoute.route,
                    getLikedSakugaPost = likedPosts,
                    onItemClick = { itemId -> navHostController.navigate(ScreenRoute.Player(itemId)) },
                    onItemDelete = onItemDelete
                )
            }

            composable<ScreenRoute.Player> { backStackEntry ->
                val itemUri = (backStackEntry.toRoute<ScreenRoute.Player>() as? ScreenRoute.Player)?.uri
                itemUri?.let {
                    SakugaPlayer(
                        navHostController = navHostController,
                        padding = padding,
                        uri = it,
                        uiState = uiState,
                        likedPosts = savedPosts,
                        sakugaTagsList = sakugaTagsList,
                        onSaveItemToDownloads = onSaveItemToDownloads,
                        onItemLiked = onItemLiked,
                        onItemDelete = onItemDelete
                    )
                }
            }
        }
    }
}
