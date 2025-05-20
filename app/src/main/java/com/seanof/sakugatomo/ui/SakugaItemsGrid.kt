package com.seanof.sakugatomo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.seanof.sakugatomo.R
import com.seanof.sakugatomo.SakugaTomoViewModel
import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.ui.navigation.ScreenRoute

@Composable
fun SakugaItemsGrid(
    padding: PaddingValues,
    uiState: SakugaTomoViewModel.ScreenUiState,
    likedPosts: List<SakugaPost>,
    currentRoute: String,
    getLikedSakugaPost: (List<SakugaPost>?, List<SakugaPost>) -> Unit,
    onItemClick: (uri: String) -> Unit = {},
    onItemDelete: (SakugaPost) -> Unit = {}) {
    Surface(
        modifier = Modifier
            .padding(top = padding.calculateTopPadding(), bottom = 0.dp)
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 2.dp, end = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is SakugaTomoViewModel.ScreenUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = colorResource(id = R.color.colorTint)
                )
                is SakugaTomoViewModel.ScreenUiState.Error -> Text(uiState.errorMessage)
                is SakugaTomoViewModel.ScreenUiState.Success -> {
                    getLikedSakugaPost.invoke(uiState.posts, likedPosts)
                    val postList =
                        if (currentRoute == ScreenRoute.Favourites.route) likedPosts else uiState.posts

                    if (postList.isNotEmpty()) {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            verticalItemSpacing = 2.dp,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            content = {
                                items(items = postList) { post ->
                                    SakugaPostCard(
                                        post,
                                        onItemClick,
                                        onItemDelete
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = padding.calculateBottomPadding()),
                        )
                    } else {
                        Text(stringResource(R.string.no_sakuga_posts_found))
                    }
                }
            }
        }
    }
}
