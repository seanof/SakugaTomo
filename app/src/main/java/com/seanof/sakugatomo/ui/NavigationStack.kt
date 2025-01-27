package com.seanof.sakugatomo.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.data.remote.SakugaApiResult

@Composable
fun NavigationStack(navHostController: NavHostController,
                    padding: PaddingValues,
                    apiResult: SakugaApiResult<List<SakugaPost>>,
                    savedPosts: List<SakugaPost>,
                    likedPosts: (List<SakugaPost>?, List<SakugaPost>) -> Unit,
                    onItemClick: (SakugaPost) -> Unit = {},
                    onItemDelete: (SakugaPost) -> Unit = {}) {

    NavHost(navController = navHostController, startDestination = Screen.Latest.route) {
        composable(route = Screen.Latest.route) {
            SakugaItemsGrid(padding, apiResult, savedPosts, Screen.Latest.route, likedPosts, onItemClick, onItemDelete)
        }
        composable(route = Screen.Liked.route) {
            SakugaItemsGrid(padding, apiResult, savedPosts, Screen.Liked.route, likedPosts, onItemClick, onItemDelete)
        }
        composable(route = Screen.Popular.route) {
            SakugaItemsGrid(padding, apiResult, savedPosts, Screen.Popular.route, likedPosts, onItemClick, onItemDelete)
        }
        composable(route = Screen.Search.route) {
            SakugaItemsGrid(padding, apiResult, savedPosts, Screen.Search.route, likedPosts, onItemClick, onItemDelete)
        }
    }
}