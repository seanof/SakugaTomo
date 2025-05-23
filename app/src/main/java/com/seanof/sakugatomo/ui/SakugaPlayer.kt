package com.seanof.sakugatomo.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.seanof.sakugatomo.R
import com.seanof.sakugatomo.SakugaTomoViewModel
import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.data.model.SakugaTag
import com.seanof.sakugatomo.util.Const.SOURCE
import com.seanof.sakugatomo.util.Const.UNKNOWN

@Composable
fun SakugaPlayer(
    padding: PaddingValues,
    uri: String,
    uiState: SakugaTomoViewModel.ScreenUiState,
    likedPosts: List<SakugaPost>,
    sakugaTagsList: List<SakugaTag>,
    onItemLiked: (SakugaPost) -> Unit = {},
    onItemDelete: (SakugaPost) -> Unit = {}
) {
    var postSaved by remember {
        mutableStateOf(false)
    }
    Surface(modifier = Modifier
        .fillMaxSize()
        .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())) {
        when (uiState) {
            is SakugaTomoViewModel.ScreenUiState.Error, SakugaTomoViewModel.ScreenUiState.Loading -> { /* not needed */ }
            is SakugaTomoViewModel.ScreenUiState.Success -> {
                val post = uiState.posts.find {
                    it.file_url == uri
                }
                if (likedPosts.contains(post)) {
                    post?.saved = true
                    postSaved = true
                }
                if (post == null) {
                    val likedPost = likedPosts.find { it.file_url == uri }
                    if (likedPost != null) {
                        post?.saved = true
                        postSaved = true
                    }
                }
                var title = UNKNOWN
                post?.tags?.split(" ")?.forEach {
                        tag -> sakugaTagsList.forEach {
                    if (it.name == tag) {
                        if (it.type == 3) {
                            if (it.name.isNotEmpty()) title = it.name
                        }
                    }
                } }

                VideoPlayer(uri)
                Box(modifier = Modifier.fillMaxSize()) {
                    if (title != UNKNOWN) Text(modifier = Modifier.align(Alignment.TopStart).padding(12.dp, 10.dp), text = "$SOURCE $title")
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .blur(radius = 0.1.dp),
                        onClick = {
                            if (post != null) {
                                if (post.saved) onItemDelete(post) else onItemLiked(post)
                                postSaved = !postSaved
                                post.saved = !post.saved
                            }
                        }) {
                        Icon(
                            imageVector = if (postSaved) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            tint = colorResource(id = R.color.heartIconTint),
                            contentDescription = stringResource(R.string.favourite_icon)
                        )
                    }
                }

            }
        }
    }
}

@SuppressLint("InflateParams")
@Composable
fun VideoPlayer(uri: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val view = LayoutInflater.from(ctx).inflate(R.layout.fixed_exo_player, null, false)
            (view as PlayerView).apply {
                clipToOutline = true
                player = exoPlayer
                useController = true
            }
            return@AndroidView view
        }
    )
}
