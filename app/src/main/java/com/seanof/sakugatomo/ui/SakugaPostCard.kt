package com.seanof.sakugatomo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.seanof.sakugatomo.R
import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.ui.shimmer.ShimmerAnimation
import com.seanof.sakugatomo.ui.theme.SakugaTomoTheme
import kotlinx.coroutines.Dispatchers

@Composable
fun SakugaPostCard(post: SakugaPost,
                   onItemClick: (uri: String) -> Unit = {},
                   onItemLiked: (SakugaPost) -> Unit = {},
                   onItemDelete: (SakugaPost) -> Unit = {}) {
    val context = LocalContext.current
    val imageRequest = ImageRequest.Builder(context)
        .data(post.preview_url)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(post.preview_url)
        .diskCacheKey(post.preview_url)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    var postSaved by remember {
        mutableStateOf(post.saved)
    }

    Box {
        SubcomposeAsyncImage(
            model = imageRequest,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            loading = {
                repeat(5) {
                    ShimmerAnimation()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clickable {
                    onItemClick(post.file_url)
                }
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .blur(radius = 0.1.dp),
            onClick = {
                if (post.saved) onItemDelete(post) else onItemLiked(post)
                postSaved = !postSaved
                post.saved = !post.saved
            }) {
            Icon(
                imageVector = if (postSaved) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                tint = colorResource(id = R.color.heartIconTint),
                contentDescription = stringResource(R.string.favourite_icon)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SakugaPostCardPreview() {
    SakugaTomoTheme {
        SakugaPostCard(SakugaPost(tags = "Test", author = "Test", id = 0))
    }
}
