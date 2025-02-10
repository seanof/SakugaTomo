package com.seanof.sakugatomo.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.seanof.sakugatomo.R
import com.seanof.sakugatomo.ui.theme.SakugaTomoTheme
import com.seanof.sakugatomo.util.Const

class SakugaViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            SakugaTomoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    intent.getStringExtra(Const.URI)?.let { VideoPlayer(it) }
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
                    useController = false
                }
                return@AndroidView view
            }
        )
    }
}
