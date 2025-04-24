package com.seanof.sakugatomo.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.seanof.sakugatomo.SakugaTomoViewModel
import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.ui.SakugaItemsGrid
import com.seanof.sakugatomo.ui.SakugaPlayer

@Composable
fun NavigationStack(navHostController: NavHostController,
                    padding: PaddingValues,
                    uiState: SakugaTomoViewModel.ScreenUiState,
                    savedPosts: List<SakugaPost>,
                    likedPosts: (List<SakugaPost>?, List<SakugaPost>) -> Unit,
                    onItemLiked: (SakugaPost) -> Unit = {},
                    onItemDelete: (SakugaPost) -> Unit = {}) {
    NavHost(navController = navHostController, startDestination = ScreenRoute.Latest.route) {
        composable(route = ScreenRoute.Latest.route) {
            SakugaItemsGrid(padding, uiState, savedPosts, ScreenRoute.Latest.route, likedPosts, { itemId -> navHostController.navigate(ScreenRoute.Player(itemId)) }, onItemLiked, onItemDelete)
        }
        composable(route = ScreenRoute.Liked.route) {
            SakugaItemsGrid(padding, uiState, savedPosts, ScreenRoute.Liked.route, likedPosts, { itemId -> navHostController.navigate(ScreenRoute.Player(itemId)) }, onItemLiked, onItemDelete)
        }
        composable(route = ScreenRoute.Popular.route) {
            SakugaItemsGrid(padding, uiState, savedPosts, ScreenRoute.Popular.route, likedPosts, { itemId -> navHostController.navigate(ScreenRoute.Player(itemId)) }, onItemLiked, onItemDelete)
        }
        composable(route = ScreenRoute.Search.route) {
            SakugaItemsGrid(padding, uiState, savedPosts, ScreenRoute.Search.route, likedPosts, { itemId -> navHostController.navigate(ScreenRoute.Player(itemId)) }, onItemLiked, onItemDelete)
        }
        composable<ScreenRoute.Player> { backStackEntry ->
            val itemUri = (backStackEntry.toRoute<ScreenRoute.Player>() as? ScreenRoute.Player)?.uri
            itemUri?.let {
                SakugaPlayer(padding, itemUri, uiState, savedPosts, onItemLiked, onItemDelete)
            }
        }
    }
}
