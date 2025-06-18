package com.seanof.sakugatomo.ui.shimmer

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat

import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.seanof.sakugatomo.ui.theme.ShimmerColors
import com.seanof.sakugatomo.util.Const.EMPTY

@Composable
fun ShimmerAnimation(
) {
    val transition = rememberInfiniteTransition(label = EMPTY)
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ), label = EMPTY
    )

    val brush = Brush.linearGradient(
        colors = ShimmerColors,
        start = Offset(10f, 10f),
        end = Offset(translateAnim, translateAnim)
    )

    ShimmerItem(brush = brush)
}