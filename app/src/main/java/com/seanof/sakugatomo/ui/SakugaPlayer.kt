package com.seanof.sakugatomo.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowDown
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.seanof.sakugatomo.R
import com.seanof.sakugatomo.SakugaTomoViewModel
import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.data.model.SakugaTag
import com.seanof.sakugatomo.util.Const.EMPTY
import com.seanof.sakugatomo.util.Const.SPACE
import com.seanof.sakugatomo.util.Const.UNDERSCORE

@Composable
fun SakugaPlayer(
    navHostController: NavHostController,
    padding: PaddingValues,
    uri: String,
    uiState: SakugaTomoViewModel.ScreenUiState,
    likedPosts: List<SakugaPost>,
    sakugaTagsList: List<SakugaTag>,
    onSaveItemToDownloads: (Context, String, String) -> Unit,
    onItemLiked: (SakugaPost) -> Unit = {},
    onItemDelete: (SakugaPost) -> Unit = {}
) {
    val context = LocalContext.current
    var postSaved by remember {
        mutableStateOf(false)
    }
    Surface(modifier = Modifier
        .fillMaxSize()
        .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())) {
        when (uiState) {
            is SakugaTomoViewModel.ScreenUiState.Error, SakugaTomoViewModel.ScreenUiState.Loading -> { /* not needed */ }
            is SakugaTomoViewModel.ScreenUiState.Success -> {
                var post = uiState.posts.find {
                    it.file_url == uri
                }
                if (likedPosts.contains(post)) {
                    post?.saved = true
                    postSaved = true
                }
                if (post == null) {
                    val likedPost = likedPosts.find { it.file_url == uri }
                    if (likedPost != null) {
                        post = likedPost
                    }
                }
                var title = stringResource(R.string.unknown)
                if (post?.sourceTitle != EMPTY) title = post?.sourceTitle.toString()
                else {
                    post.tags.split(SPACE).forEach { tag ->
                        sakugaTagsList.forEach {
                            if (it.name == tag) {
                                if (it.type == 3) {
                                    if (it.name.isNotEmpty()) {
                                        title = it.name.replace(UNDERSCORE, SPACE)
                                        post.sourceTitle = title
                                    }
                                }
                            }
                        }
                    }
                }

                VideoPlayer(uri)
                Row(
                    modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            modifier = Modifier
                                .blur(radius = 0.1.dp),
                            onClick = {
                                navHostController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                tint = colorResource(id = R.color.colorTint),
                                contentDescription = stringResource(R.string.back_icon)
                            )
                        }
                        Row(
                            modifier = Modifier.weight(0.7f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            if (title != stringResource(R.string.unknown) || title != EMPTY) {
                                Text(
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    text = stringResource(R.string.source, title)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.wrapContentWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                modifier = Modifier
                                    .blur(radius = 0.1.dp),
                                onClick = {
                                    if (post != null) {
                                        onSaveItemToDownloads(context, uri, uri.toString())
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_download),
                                    tint = colorResource(id = R.color.colorTint),
                                    contentDescription = stringResource(R.string.download_icon)
                                )
                            }
                            IconButton(
                                modifier = Modifier
                                    .blur(radius = 0.1.dp),
                                onClick = {
                                    if (post != null) {
                                        if (post.saved) onItemDelete(post) else onItemLiked(post)
                                        postSaved = !postSaved
                                        post.saved = !post.saved
                                    }
                                }
                            ) {
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
