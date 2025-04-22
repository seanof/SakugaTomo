package com.seanof.sakugatomo.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.seanof.sakugatomo.SakugaTomoViewModel
import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.ui.SakugaItemsGrid

@Composable
fun NavigationStack(navHostController: NavHostController,
                    padding: PaddingValues,
                    uiState: SakugaTomoViewModel.ScreenUiState,
                    savedPosts: List<SakugaPost>,
                    likedPosts: (List<SakugaPost>?, List<SakugaPost>) -> Unit,
                    onItemClick: (SakugaPost) -> Unit = {},
                    onItemDelete: (SakugaPost) -> Unit = {}) {

    NavHost(navController = navHostController, startDestination = ScreenRoute.Latest.route) {
        composable(route = ScreenRoute.Latest.route) {
            SakugaItemsGrid(padding, uiState, savedPosts, ScreenRoute.Latest.route, likedPosts, onItemClick, onItemDelete)
        }
        composable(route = ScreenRoute.Liked.route) {
            SakugaItemsGrid(padding, uiState, savedPosts, ScreenRoute.Liked.route, likedPosts, onItemClick, onItemDelete)
        }
        composable(route = ScreenRoute.Popular.route) {
            SakugaItemsGrid(padding, uiState, savedPosts, ScreenRoute.Popular.route, likedPosts, onItemClick, onItemDelete)
        }
        composable(route = ScreenRoute.Search.route) {
            SakugaItemsGrid(padding, uiState, savedPosts, ScreenRoute.Search.route, likedPosts, onItemClick, onItemDelete)
        }
    }
}
