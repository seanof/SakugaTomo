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

@Composable
fun SakugaPlayer(
    padding: PaddingValues,
    uri: String,
    uiState: SakugaTomoViewModel.ScreenUiState,
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
                VideoPlayer(uri)
                Box(modifier = Modifier.fillMaxSize()) {
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
